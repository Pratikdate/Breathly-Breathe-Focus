package com.shanacoder.breathly

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shanacoder.breathly.data.AppDatabase
import com.shanacoder.breathly.data.PatternEntity
import com.shanacoder.breathly.data.SessionEntity
import com.shanacoder.breathly.data.SettingsManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class CustomPattern(
    val name: String,
    val inhale: Float,
    val hold1: Float,
    val exhale: Float,
    val hold2: Float,
    val cycles: Int = 4,
    val colorHex: Long = 0xFF9FC4A8,
    val isFavorite: Boolean = false,
    val id: Int = 0,
    val description: String = "",
    val benefits: String = "",
    val methods: String = ""
)

data class SessionStats(
    val totalSessions: Int = 0,
    val totalSeconds: Int = 0,
    val streak: Int = 0
)

data class BreathHoldStats(
    val avgHoldSeconds: Int = 0,
    val personalBestSeconds: Int = 0
)

data class ChartData(
    val bars: List<Float>,
    val labels: List<String>,
    val total: Float,
    val avg: Float
)

class BreathlyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val settingsManager = SettingsManager(application)

    val customPatterns: StateFlow<List<CustomPattern>> = db.patternDao().getAllPatterns()
        .map { list ->
            list.sortedByDescending { it.isFavorite }
                .map { CustomPattern(it.name, it.inhale, it.hold1, it.exhale, it.hold2, it.cycles, it.colorHex, it.isFavorite, it.id, it.description, it.benefits, it.methods) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val stats: StateFlow<SessionStats> = db.sessionDao().getAllSessions()
        .map { sessions ->
            val totalSec = sessions.sumOf { it.durationSeconds }
            val streak = calculateStreak(sessions.map { it.timestamp })
            SessionStats(
                totalSessions = sessions.size,
                totalSeconds = totalSec,
                streak = streak
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionStats())

    val breathHoldStats: StateFlow<BreathHoldStats> = db.sessionDao().getAllSessions()
        .map { sessions ->
            val holdSessions = sessions.filter { 
                it.patternName.equals("Breath Holding Test", ignoreCase = true) ||
                it.patternName.equals("Breath Hold Test", ignoreCase = true)
            }
            val best = holdSessions.maxOfOrNull { it.durationSeconds } ?: 0
            val avg = if (holdSessions.isNotEmpty()) holdSessions.map { it.durationSeconds }.average().toInt() else 0
            BreathHoldStats(
                avgHoldSeconds = avg,
                personalBestSeconds = best
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreathHoldStats())

    val progressSectionOrder: StateFlow<List<String>> = settingsManager.progressSectionOrder
        .map { it.split(",") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Stats", "Chart", "BreathHold"))

    val homeSectionOrder: StateFlow<List<String>> = settingsManager.homeSectionOrder
        .map { it.split(",") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("Equal", "Box", "478", "Hold"))

    private fun calculateStreak(timestamps: List<Long>): Int {
        if (timestamps.isEmpty()) return 0
        val cal = Calendar.getInstance()
        val today = getDayStart(cal)
        val uniqueDays = timestamps
            .map { getDayStart(Calendar.getInstance().also { c -> c.timeInMillis = it }) }
            .toSortedSet(reverseOrder())
        var streak = 0
        var expected = today
        for (day in uniqueDays) {
            if (day == expected) {
                streak++
                expected -= TimeUnit.DAYS.toMillis(1)
            } else if (day < expected) break
        }
        return streak
    }

    private fun getDayStart(cal: Calendar): Long {
        return Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    suspend fun getChartData(tab: String, offsetPeriods: Int = 0): ChartData {
        val cal = Calendar.getInstance()
        val bars = mutableListOf<Float>()
        val labels = mutableListOf<String>()

        when (tab) {
            "Day" -> {
                cal.add(Calendar.DAY_OF_YEAR, -offsetPeriods)
                val dayStart = getDayStart(cal)
                val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1)
                val sessions = db.sessionDao().getSessionsBetween(dayStart, dayEnd)
                val hourBuckets = FloatArray(24) { 0f }
                sessions.forEach { s ->
                    val hr = Calendar.getInstance().also { it.timeInMillis = s.timestamp }.get(Calendar.HOUR_OF_DAY)
                    hourBuckets[hr] += s.durationSeconds / 60f
                }
                (0..23 step 4).forEach { h ->
                    bars.add((h until (h + 4).coerceAtMost(24)).sumOf { hourBuckets[it].toDouble() }.toFloat())
                    labels.add("${h}h")
                }
            }
            "Week" -> {
                // Set to beginning of today
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                
                // Find the Monday of the current week
                while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                    cal.add(Calendar.DAY_OF_YEAR, -1)
                }
                
                // Apply offset
                cal.add(Calendar.WEEK_OF_YEAR, -offsetPeriods)
                
                val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                for (i in 0..6) {
                    val from = cal.timeInMillis
                    val to = from + TimeUnit.DAYS.toMillis(1)
                    val sessions = db.sessionDao().getSessionsBetween(from, to)
                    bars.add(sessions.sumOf { it.durationSeconds } / 60f)
                    labels.add(dayNames[i])
                    cal.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            "Month" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.MONTH, -offsetPeriods)
                for (w in 1..4) {
                    val from = getDayStart(cal)
                    val to = from + TimeUnit.DAYS.toMillis(7)
                    val sessions = db.sessionDao().getSessionsBetween(from, to)
                    bars.add(sessions.sumOf { it.durationSeconds } / 60f)
                    labels.add("W$w")
                    cal.add(Calendar.DAY_OF_YEAR, 7)
                }
            }
            "Year" -> {
                cal.set(Calendar.MONTH, Calendar.JANUARY)
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.add(Calendar.YEAR, -offsetPeriods)
                val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
                for (m in 0..11) {
                    val from = getDayStart(cal)
                    cal.add(Calendar.MONTH, 1)
                    val to = getDayStart(cal)
                    val sessions = db.sessionDao().getSessionsBetween(from, to)
                    bars.add(sessions.sumOf { it.durationSeconds } / 60f)
                    labels.add(monthNames[m])
                }
            }
        }

        val total = bars.sum()
        val avg = if (bars.isNotEmpty()) total / bars.size else 0f
        return ChartData(bars, labels, total, avg)
    }

    fun addCustomPattern(pattern: CustomPattern) {
        viewModelScope.launch {
            db.patternDao().insertPattern(
                PatternEntity(
                    name = pattern.name,
                    inhale = pattern.inhale,
                    hold1 = pattern.hold1,
                    exhale = pattern.exhale,
                    hold2 = pattern.hold2,
                    cycles = pattern.cycles,
                    colorHex = pattern.colorHex,
                    isFavorite = pattern.isFavorite,
                    description = pattern.description,
                    benefits = pattern.benefits,
                    methods = pattern.methods
                )
            )
        }
    }

    fun deleteCustomPattern(id: Int) {
        viewModelScope.launch {
            db.patternDao().deletePattern(id)
        }
    }

    fun recordSession(durationSeconds: Int, patternName: String) {
        viewModelScope.launch {
            db.sessionDao().insertSession(
                SessionEntity(
                    patternName = patternName,
                    cycles = 0,
                    durationSeconds = durationSeconds,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun updateProgressSectionOrder(order: List<String>) {
        viewModelScope.launch {
            settingsManager.setProgressSectionOrder(order.joinToString(","))
        }
    }

    fun updateHomeSectionOrder(order: List<String>) {
        viewModelScope.launch {
            settingsManager.setHomeSectionOrder(order.joinToString(","))
        }
    }
}
