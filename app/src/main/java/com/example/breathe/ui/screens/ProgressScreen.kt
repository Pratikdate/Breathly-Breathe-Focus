package com.shanacoder.breathly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shanacoder.breathly.BreathlyViewModel
import com.shanacoder.breathly.ChartData
import com.shanacoder.breathly.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProgressScreen(
    viewModel: BreathlyViewModel,
    bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
    onNavigateToSettings: () -> Unit,
    onRequestReview: () -> Unit = {}
) {
    val stats by viewModel.stats.collectAsState()
    val breathHoldStats by viewModel.breathHoldStats.collectAsState()

    var selectedTab by remember { mutableStateOf("Week") }
    var offsetPeriods by remember { mutableStateOf(0) }
    var chartData by remember { mutableStateOf<ChartData?>(null) }
    var isLiked by remember { mutableStateOf(false) }

    // Re-load chart data whenever tab or offset changes
    LaunchedEffect(selectedTab, offsetPeriods, stats) {
        chartData = viewModel.getChartData(selectedTab, offsetPeriods)
    }

    val totalMinutes = stats.totalSeconds / 60f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
    ) {
        // ── Top App Bar ───────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            // Left: Favorite
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorite",
                tint = if (isLiked) Color(0xFFFF5252) else TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable {
                        isLiked = !isLiked
                        onRequestReview()
                    }
            )

            // Center: Title
            Text(
                "Breathly",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Right: Settings
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "Settings",
                tint = TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clickable { onNavigateToSettings() }
            )
        }

        // ── Content (Scrollable) ──────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SectionWrapper(title = "Progress Chart") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(10.dp))
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        val tabs = listOf("Day", "Week", "Month", "Year")
                        tabs.forEachIndexed { i, tab ->
                            val isSelected = tab == selectedTab
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Color(0xFF00BCD4) else Color.Transparent)
                                    .clickable {
                                        selectedTab = tab
                                        offsetPeriods = 0
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(tab, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Color.White else TextSecondary)
                            }
                            if (i < tabs.lastIndex) {
                                Divider(modifier = Modifier.height(36.dp).width(1.dp), color = Color(0xFFE8E8E8))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total / Avg summary
                    chartData?.let { data ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text("TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.1f", data.total)}m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                            Column {
                                Text("AVG", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text("${String.format("%.1f", data.avg)}m", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Date Navigation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier.size(32.dp).border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp)).clickable {
                                offsetPeriods++
                            }, contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Prev", tint = TextSecondary, modifier = Modifier.size(18.dp)) }

                        Text(
                            text = when {
                                offsetPeriods == 0 -> "This ${selectedTab}"
                                offsetPeriods == 1 -> "Last ${selectedTab}"
                                else -> "$offsetPeriods ${selectedTab}s ago"
                            },
                            fontSize = 13.sp, color = TextSecondary
                        )

                        Box(
                            modifier = Modifier.size(32.dp)
                                .border(1.dp, if (offsetPeriods > 0) Color(0xFFE0E0E0) else Color(0xFFE0E0E0).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable(enabled = offsetPeriods > 0) { if (offsetPeriods > 0) offsetPeriods-- },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = if (offsetPeriods > 0) TextSecondary else Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(18.dp)) }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bar Chart
                    chartData?.let { data ->
                        if (data.bars.all { it == 0f }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No data yet",
                                    color = Color(0xFF00BCD4),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            BarChart(data = data)
                        }
                    } ?: Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00BCD4), modifier = Modifier.size(24.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Breathing Exercise Stats
            SectionWrapper(title = "Breathing Exercise") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStatItem(stats.totalSessions.toString(), "Sessions")
                    StatDivider()
                    ProgressStatItem(String.format("%.1f", totalMinutes), "Minutes")
                    StatDivider()
                    ProgressStatItem("${stats.streak}d", "Streak")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Breath Hold Test
            SectionWrapper(title = "Breath Hold Test") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStatItem("${breathHoldStats.avgHoldSeconds}s", "Avg Hold")
                    StatDivider()
                    ProgressStatItem("${breathHoldStats.personalBestSeconds}s", "Personal Best")
                }
            }

            Spacer(modifier = Modifier.height(bottomPadding + 16.dp))
        }
    }
}

@Composable
fun SectionWrapper(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun BarChart(data: ChartData) {
    val maxVal = data.bars.maxOrNull()?.coerceAtLeast(0.1f) ?: 0.1f
    val tealColor = Color(0xFF4DD0C4)
    val gridColor = Color(0xFFF0F0F0)

    Column {
        Row(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            // Y-axis labels
            Column(
                modifier = Modifier.fillMaxHeight().padding(end = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                listOf(maxVal, maxVal / 2f, 0f).forEach {
                    Text("${String.format("%.1f", it)}m", fontSize = 9.sp, color = Color.Gray)
                }
            }

            // Bars + grid lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val barWidth = size.width / (data.bars.size * 1.6f)
                val spacing = size.width / data.bars.size
                val availableHeight = size.height

                // Draw 3 horizontal grid lines
                listOf(0f, 0.5f, 1f).forEach { frac ->
                    val y = availableHeight * (1f - frac)
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.5f)
                }

                data.bars.forEachIndexed { i, value ->
                    val barHeight = (value / maxVal) * availableHeight
                    val x = spacing * i + (spacing - barWidth) / 2
                    val y = availableHeight - barHeight
                    if (barHeight > 0) {
                        drawRoundRect(
                            color = tealColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6f, 6f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // X-axis labels
        Row(modifier = Modifier.fillMaxWidth().padding(start = 28.dp)) {
            data.labels.forEach { label ->
                Text(label, modifier = Modifier.weight(1f), fontSize = 9.sp, color = Color.Gray, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
    }
}

@Composable
fun StatDivider() {
    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color(0xFFF0F0F0))
}

@Composable
fun ProgressStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 30.sp, fontWeight = FontWeight.Light, color = TextPrimary)
    }
}
