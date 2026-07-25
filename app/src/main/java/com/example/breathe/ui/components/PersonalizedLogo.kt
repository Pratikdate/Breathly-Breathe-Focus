package com.shanacoder.breathly.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.shanacoder.breathly.ui.theme.OrganicBlobShape
import kotlinx.coroutines.delay

enum class LogoState {
    ABSENCE, RECOVERY, ACTIVE
}

@Composable
fun PersonalizedLogo(
    isIgnored: Boolean,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(if (isIgnored) LogoState.ABSENCE else LogoState.ACTIVE) }

    // On first launch, if ignored, trigger Recovery Sigh, then Active
    LaunchedEffect(isIgnored) {
        if (isIgnored && state == LogoState.ABSENCE) {
            delay(1000) // Stay sluggish for a moment so user sees the "Absence" state
            state = LogoState.RECOVERY
            delay(2500) // Profound deep breath duration
            state = LogoState.ACTIVE
        }
    }

    val targetColor = if (state == LogoState.ABSENCE) Color(0xFFC3D6A9) else Color(0xFFE4F087)
    val blobColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(1500, easing = LinearOutSlowInEasing),
        label = "blobColor"
    )

    // Infinite pulse animations
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val activeScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "activePulse"
    )

    val absenceScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0.93f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearOutSlowInEasing), // Sluggish, slower movement
            repeatMode = RepeatMode.Reverse
        ),
        label = "absencePulse"
    )

    // Recovery breath animation (one-shot)
    val recoveryScale = remember { Animatable(0.9f) }
    LaunchedEffect(state) {
        if (state == LogoState.RECOVERY) {
            // Profound expansion
            recoveryScale.animateTo(
                targetValue = 1.3f,
                animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
            )
            // Contraction
            recoveryScale.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }

    val currentScale = when (state) {
        LogoState.ABSENCE -> absenceScale
        LogoState.RECOVERY -> recoveryScale.value
        LogoState.ACTIVE -> activeScale
    }

    // Heavy absence pushes the logo downwards, Active centers it
    val targetYOffset = if (state == LogoState.ABSENCE) 50f else 0f
    val currentYOffset by animateFloatAsState(
        targetValue = targetYOffset, 
        animationSpec = tween(2000, easing = FastOutSlowInEasing), 
        label = "yOffset"
    )

    // Glow effect only visible in Active/Recovery states
    val glowAlpha = if (state == LogoState.ABSENCE) 0f else 0.5f
    val currentGlowAlpha by animateFloatAsState(
        targetValue = glowAlpha, 
        animationSpec = tween(1500), 
        label = "glowAlpha"
    )
    
    val currentGlowScale = currentScale * 1.5f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow layer
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    scaleX = currentGlowScale
                    scaleY = currentGlowScale
                    translationY = currentYOffset
                    alpha = currentGlowAlpha
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            blobColor.copy(alpha = 1f),
                            blobColor.copy(alpha = 0f)
                        )
                    )
                )
        )

        // Solid blob layer
        Box(
            modifier = Modifier
                .size(120.dp)
                .graphicsLayer {
                    scaleX = currentScale
                    // Appears slightly squashed horizontally when "Heavy"
                    scaleY = if (state == LogoState.ABSENCE) currentScale * 0.85f else currentScale
                    translationY = currentYOffset
                }
                .clip(OrganicBlobShape())
                .background(blobColor)
        )
    }
}
