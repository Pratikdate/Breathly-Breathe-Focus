package com.shanacoder.breathly.ui.screens

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shanacoder.breathly.audio.AudioHapticManager
import com.shanacoder.breathly.data.SettingsManager
import com.shanacoder.breathly.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class BreathPhase { INHALE, HOLD1, EXHALE, HOLD2 }

@Composable
fun SessionScreen(
    name: String,
    inhale: Float,
    hold1: Float,
    exhale: Float,
    hold2: Float,
    cycles: Int,
    onSessionComplete: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    onRequestReview: () -> Unit = {}
) {
    var currentCycle by remember { mutableStateOf(1) }
    var isPaused by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableStateOf(0) }
    var showCompletionDialog by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf(BreathPhase.INHALE) }
    var phaseText by remember { mutableStateOf("Breathe In") }

    val scaleAnim = remember { Animatable(1f) }

    val context = LocalContext.current
    val audioHapticManager = remember { AudioHapticManager(context) }
    val settingsManager = remember { SettingsManager(context) }
    val notificationManager = remember { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    val scope = rememberCoroutineScope()
    var originalFilter by remember { mutableIntStateOf(NotificationManager.INTERRUPTION_FILTER_ALL) }
    var dndWasEnabled by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        scope.launch {
            dndWasEnabled = settingsManager.dndModeEnabled.first()
            if (dndWasEnabled && notificationManager.isNotificationPolicyAccessGranted) {
                originalFilter = notificationManager.currentInterruptionFilter
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            }
        }

        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            audioHapticManager.stopNatureSound()
            audioHapticManager.release()
            
            if (dndWasEnabled && notificationManager.isNotificationPolicyAccessGranted) {
                notificationManager.setInterruptionFilter(originalFilter)
            }
        }
    }

    LaunchedEffect(isPaused, currentCycle) {
        val rhythmOn = settingsManager.followRhythmEnabled.first()
        val hapticsOn = settingsManager.hapticsEnabled.first()
        val soundsOn = settingsManager.soundsEnabled.first()
        audioHapticManager.startNatureSound(soundsOn)

        if (!isPaused) {
            while (currentCycle <= cycles) {
                // INHALE
                currentPhase = BreathPhase.INHALE
                phaseText = "Breathe In"
                if (inhale > 0) {
                    audioHapticManager.triggerHaptic(hapticsOn)
                    audioHapticManager.playVoice("Breathe in", rhythmOn)
                    scaleAnim.animateTo(
                        targetValue = 1.15f,
                        animationSpec = tween(durationMillis = (inhale * 1000).toInt(), easing = LinearOutSlowInEasing)
                    )
                }

                // HOLD 1
                if (!isPaused && hold1 > 0) {
                    currentPhase = BreathPhase.HOLD1
                    phaseText = "Hold"
                    audioHapticManager.triggerHaptic(hapticsOn)
                    audioHapticManager.playVoice("Hold", rhythmOn)
                    delay((hold1 * 1000).toLong())
                }

                // EXHALE
                if (!isPaused) {
                    currentPhase = BreathPhase.EXHALE
                    phaseText = "Breathe Out"
                    if (exhale > 0) {
                        audioHapticManager.triggerHaptic(hapticsOn)
                        audioHapticManager.playVoice("Breathe out", rhythmOn)
                        scaleAnim.animateTo(
                            targetValue = 0.85f,
                            animationSpec = tween(durationMillis = (exhale * 1000).toInt(), easing = LinearEasing)
                        )
                    }
                }

                // HOLD 2
                if (!isPaused && hold2 > 0) {
                    currentPhase = BreathPhase.HOLD2
                    phaseText = "Hold"
                    audioHapticManager.triggerHaptic(hapticsOn)
                    audioHapticManager.playVoice("Hold", rhythmOn)
                    delay((hold2 * 1000).toLong())
                }

                if (!isPaused) {
                    if (currentCycle < cycles) {
                        currentCycle++
                    } else {
                        showCompletionDialog = true
                        break
                    }
                }
            }
        }
    }

    // Timer tick
    LaunchedEffect(isPaused) {
        if (!isPaused) {
            while (true) {
                delay(1000L)
                secondsElapsed++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F5F0))
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clickable { onNavigateBack() }
            )
            val minutes = secondsElapsed / 60
            val seconds = secondsElapsed % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        Text(
            text = "$currentCycle / $cycles",
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 12.dp),
            fontSize = 16.sp,
            color = TextSecondary
        )

        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .scale(scaleAnim.value)
                    .clip(OrganicBlobShape())
                    .background(Color(0xFFDCEB8F)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = phaseText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        color = Color(0xFF555555)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { isPaused = !isPaused },
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8CBF9F))
        ) {
            Icon(if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isPaused) "Resume" else "Pause")
        }
    }

    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = {
                onSessionComplete(secondsElapsed)
            },
            title = { Text("Session Completed! 🌸", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Wonderful job! You completed $cycles cycles of $name.\n\nEnjoying Breathly? Leaving a review on Google Play helps others discover calm and focus.",
                    fontSize = 14.sp,
                    color = Color(0xFF555555)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRequestReview()
                        onSessionComplete(secondsElapsed)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF8A))
                ) {
                    Text("Rate on Play Store", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onSessionComplete(secondsElapsed)
                    }
                ) {
                    Text("Done", color = Color.Gray)
                }
            }
        )
    }
}

private fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) return currentContext
        currentContext = currentContext.baseContext
    }
    return null
}
