package com.shanacoder.breathly.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.shanacoder.breathly.BreathlyViewModel
import com.shanacoder.breathly.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    viewModel: BreathlyViewModel,
    bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
    onNavigateToSession: (name: String, inhale: Float, hold1: Float, exhale: Float, hold2: Float, cycles: Int) -> Unit,
    onNavigateToSettings: () -> Unit,
    onRequestReview: () -> Unit
) {
    val customPatterns by viewModel.customPatterns.collectAsState()
    val homeSectionOrder by viewModel.homeSectionOrder.collectAsState()

    // Map of ID to Card Definition
    val presetsMap = mapOf(
        "Equal" to CardDef("Equal\nBreathing", "Equal Breathing helps you relax and focus.", listOf("4s", "4s"), 5, CardSage, CardSageText, 0) { c -> onNavigateToSession("Equal Breathing", 4f, 0f, 4f, 0f, c) },
        "Box" to CardDef("Box\nBreathing", "Box Breathing is a powerful stress reliever.", listOf("4s", "4s", "4s", "4s"), 4, CardSky, CardSkyText, 1) { c -> onNavigateToSession("Box Breathing", 4f, 4f, 4f, 4f, c) },
        "478" to CardDef("4-7-8\nBreathing", "4-7-8 Breathing promotes better sleep.", listOf("4s", "7s", "8s"), 3, CardPeach, CardPeachText, 2) { c -> onNavigateToSession("4-7-8 Breathing", 4f, 7f, 8f, 0f, c) },
        "Hold" to CardDef("Breath\nHolding Test", "Test your breath-holding capacity.", listOf("4s", "4s"), 1, CardPink, CardPinkText, 3) { c -> onNavigateToSession("Breath Holding Test", 4f, 0f, 4f, 0f, c) }
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
            .statusBarsPadding()
    ) {
        val screenH = maxHeight
        val screenW = maxWidth
        val appBarH = 56.dp
        val gap = 16.dp
        val cardH = ((screenH - appBarH - gap * 3) / 2).coerceAtMost(210.dp)
        val cardW = (screenW - gap * 3) / 2

        Column(modifier = Modifier.fillMaxSize()) {

            // ── AppBar ───────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(appBarH)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.FavoriteBorder,
                    contentDescription = "Rate App",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.CenterStart)
                        .clickable { onRequestReview() }
                )
                Text("Breathly", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
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

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Interactive Drag-and-Drop Preset Grid
                item {
                    var localOrder by remember(homeSectionOrder) { mutableStateOf(homeSectionOrder) }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardH * 2 + gap)
                    ) {
                        var draggedIndex by remember { mutableStateOf<Int?>(null) }
                        var dragOffset by remember { mutableStateOf(Offset.Zero) }

                        val density = LocalDensity.current
                        val cardWPx = with(density) { cardW.toPx() }
                        val cardHPx = with(density) { cardH.toPx() }
                        val gapPx = with(density) { gap.toPx() }

                        val slotOffsets = listOf(
                            Offset(0f, 0f),
                            Offset(cardWPx + gapPx, 0f),
                            Offset(0f, cardHPx + gapPx),
                            Offset(cardWPx + gapPx, cardHPx + gapPx)
                        )

                        localOrder.forEachIndexed { index, key ->
                            val def = presetsMap[key]
                            if (def != null) {
                                val isDragged = index == draggedIndex
                                val slotOffset = slotOffsets[index]
                                
                                val animX by animateFloatAsState(targetValue = slotOffset.x, label = "x")
                                val animY by animateFloatAsState(targetValue = slotOffset.y, label = "y")
                                val scale by animateFloatAsState(if (isDragged) 1.05f else 1f, label = "scale")
                                val zIndex = if (isDragged) 10f else 1f

                                Box(
                                    modifier = Modifier
                                        .size(width = cardW, height = cardH)
                                        .zIndex(zIndex)
                                        .graphicsLayer {
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .offset {
                                            val x = if (isDragged) slotOffset.x + dragOffset.x else animX
                                            val y = if (isDragged) slotOffset.y + dragOffset.y else animY
                                            IntOffset(x.roundToInt(), y.roundToInt())
                                        }
                                        .pointerInput(index) {
                                            detectDragGesturesAfterLongPress(
                                                onDragStart = {
                                                    draggedIndex = index
                                                    dragOffset = Offset.Zero
                                                },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    dragOffset += dragAmount
                                                },
                                                onDragEnd = {
                                                    val visualOffset = slotOffset + dragOffset
                                                    val targetSlot = slotOffsets.indices.minByOrNull { slotIdx ->
                                                        val slot = slotOffsets[slotIdx]
                                                        val dx = visualOffset.x - slot.x
                                                        val dy = visualOffset.y - slot.y
                                                        dx * dx + dy * dy
                                                    } ?: index

                                                    if (targetSlot != index && targetSlot in slotOffsets.indices) {
                                                        val newList = localOrder.toMutableList()
                                                        val temp = newList[index]
                                                        newList[index] = newList[targetSlot]
                                                        newList[targetSlot] = temp
                                                        localOrder = newList
                                                        viewModel.updateHomeSectionOrder(newList)
                                                    }

                                                    draggedIndex = null
                                                    dragOffset = Offset.Zero
                                                },
                                                onDragCancel = {
                                                    draggedIndex = null
                                                    dragOffset = Offset.Zero
                                                }
                                            )
                                        }
                                ) {
                                    ExerciseCard(def = def, modifier = Modifier.fillMaxSize())
                                }
                            }
                        }
                    }
                }

                // Custom patterns section
                if (customPatterns.isNotEmpty()) {
                    item {
                        Text(
                            "Your Custom Patterns",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    val chunks = customPatterns.chunked(2)
                    items(chunks.size) { rowIndex ->
                        val pair = chunks[rowIndex]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(gap)
                        ) {
                            pair.forEachIndexed { i, pattern ->
                                val timesList = buildList {
                                    if (pattern.inhale > 0) add("${pattern.inhale.toInt()}s")
                                    if (pattern.hold1 > 0) add("${pattern.hold1.toInt()}s")
                                    if (pattern.exhale > 0) add("${pattern.exhale.toInt()}s")
                                    if (pattern.hold2 > 0) add("${pattern.hold2.toInt()}s")
                                }
                                ExerciseCard(
                                    def = CardDef(
                                        title = pattern.name.replace(" ", "\n"),
                                        description = pattern.description.ifEmpty { "Custom pattern" },
                                        times = timesList,
                                        defaultCycles = pattern.cycles,
                                        color = Color(pattern.colorHex),
                                        textColor = CardSageText,
                                        shapeIndex = 4 + (rowIndex * 2 + i),
                                        onStart = { c -> onNavigateToSession(pattern.name, pattern.inhale, pattern.hold1, pattern.exhale, pattern.hold2, c) }
                                    ),
                                    modifier = Modifier.size(width = cardW, height = cardH)
                                )
                            }
                            if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(bottomPadding + 16.dp)) }
            }
        }
    }
}

data class CardDef(
    val title: String,
    val description: String,
    val times: List<String>,
    val defaultCycles: Int,
    val color: Color,
    val textColor: Color,
    val shapeIndex: Int,
    val onStart: (Int) -> Unit
)

@Composable
fun ExerciseCard(def: CardDef, modifier: Modifier = Modifier) {
    var currentCycles by remember { mutableStateOf(def.defaultCycles) }
    val cycleDuration = when (def.shapeIndex) {
        0 -> "${String.format("%d:%02d", currentCycles * 8 / 60, currentCycles * 8 % 60)}"
        1 -> "${String.format("%d:%02d", currentCycles * 16 / 60, currentCycles * 16 % 60)}"
        2 -> "${String.format("%d:%02d", currentCycles * 19 / 60, currentCycles * 19 % 60)}"
        else -> "${String.format("%d:%02d", currentCycles * 8 / 60, currentCycles * 8 % 60)}"
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = OrganicCardShape(def.shapeIndex),
                spotColor = def.color.copy(alpha = 0.4f),
                ambientColor = def.color.copy(alpha = 0.2f)
            )
            .clip(OrganicCardShape(def.shapeIndex))
            .background(def.color)
            .clickable { def.onStart(currentCycles) }
    ) {
        // Subtle inner highlight — top-left glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                        radius = 350f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Title + Description
            Column {
                Text(
                    text = def.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = def.textColor,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = def.description,
                    fontSize = 11.sp,
                    color = def.textColor.copy(alpha = 0.75f),
                    lineHeight = 15.sp
                )
            }

            // Middle: phase chips + cycle info
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    def.times.take(4).forEach { time ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.28f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(text = time, fontSize = 10.sp, color = def.textColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "⊙ $currentCycles Cycles • $cycleDuration",
                    fontSize = 11.sp,
                    color = def.textColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }

            // Bottom: counter + start
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CounterButton("-", def.textColor) { if (currentCycles > 1) currentCycles-- }
                    Text("$currentCycles", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = def.textColor)
                    CounterButton("+", def.textColor) { currentCycles++ }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.38f))
                        .clickable { def.onStart(currentCycles) }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶", fontSize = 13.sp, color = def.textColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CounterButton(label: String, textColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.3f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = textColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}
