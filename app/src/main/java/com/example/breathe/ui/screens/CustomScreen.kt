package com.shanacoder.breathly.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shanacoder.breathly.BreathlyViewModel
import com.shanacoder.breathly.CustomPattern
import com.shanacoder.breathly.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScreen(
    viewModel: BreathlyViewModel,
    bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
    onNavigateHome: () -> Unit
) {
    val stats by viewModel.stats.collectAsState()
    val breathHoldStats by viewModel.breathHoldStats.collectAsState()

    var exerciseName by remember { mutableStateOf("") }
    var exerciseDescription by remember { mutableStateOf("") }
    var exerciseBenefits by remember { mutableStateOf("") }
    var exerciseMethods by remember { mutableStateOf("") }
    
    var inhale by remember { mutableStateOf(4.0f) }
    var hold1 by remember { mutableStateOf(4.0f) }
    var exhale by remember { mutableStateOf(4.0f) }
    var hold2 by remember { mutableStateOf(4.0f) }
    var cycles by remember { mutableStateOf(4) }
    var cardColor by remember { mutableStateOf(0xFF9FC4A8) } // Default Sage

    val suggestions = remember(stats.totalSessions, breathHoldStats.personalBestSeconds) {
        val list = mutableListOf(
            SuggestedPattern("Resonant", "5.5 - 0 - 5.5 - 0", 5.5f, 0f, 5.5f, 0f, 0xFF9FC4A8),
            SuggestedPattern("Box", "4 - 4 - 4 - 4", 4f, 4f, 4f, 4f, 0xFF9ECDE0),
            SuggestedPattern("Calm", "4 - 7 - 8 - 0", 4f, 7f, 8f, 0f, 0xFFE8C4A0),
            SuggestedPattern("Uplift", "6 - 0 - 2 - 0", 6f, 0f, 2f, 0f, 0xFFD4A8C0)
        )
        if (breathHoldStats.personalBestSeconds > 20) {
            list.add(SuggestedPattern("Deep", "4 - 8 - 8 - 4", 4f, 8f, 8f, 4f, 0xFF4A7C6B))
        }
        if (stats.totalSessions > 10) {
            list.add(SuggestedPattern("Advanced", "5 - 5 - 10 - 0", 5f, 5f, 10f, 0f, 0xFF5B8FA8))
        }
        list
    }

    var selectedSuggestion by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.addCustomPattern(
                        CustomPattern(
                            name = exerciseName.ifEmpty { "Custom Breath" },
                            inhale = inhale,
                            hold1 = hold1,
                            exhale = exhale,
                            hold2 = hold2,
                            cycles = cycles,
                            colorHex = cardColor,
                            description = exerciseDescription,
                            benefits = exerciseBenefits,
                            methods = exerciseMethods
                        )
                    )
                    onNavigateHome()
                },
                containerColor = Color(0xFF90E0EF),
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = bottomPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            }
        },
        topBar = {
            Column(modifier = Modifier.background(BackgroundColor).statusBarsPadding()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterStart)
                            .clickable { onNavigateHome() }
                    )
                    Text(
                        "New Pattern",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = exerciseName,
                onValueChange = { exerciseName = it },
                label = { Text("Exercise Name*", color = Teal) },
                placeholder = { Text("e.g. Morning Focus", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB0B0B0),
                    unfocusedBorderColor = Color(0xFFB0B0B0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            var infoExpanded by remember { mutableStateOf(true) }

            // Exercise Information Accordion
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp).clickable { infoExpanded = !infoExpanded },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Teal)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Exercise Information", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Details, Benefits & Methods", fontSize = 12.sp, color = TextMuted)
                        }
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (infoExpanded) "Collapse" else "Expand",
                            tint = TextSecondary,
                            modifier = Modifier.graphicsLayer {
                                rotationZ = if (infoExpanded) 180f else 0f
                            }
                        )
                    }

                    if (infoExpanded) {
                        Divider(color = Color(0xFFF0F0F0))
                        Column(modifier = Modifier.padding(16.dp)) {
                            CustomInputField("Description", exerciseDescription, "Short summary of the exercise...") { exerciseDescription = it }
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomInputField("Benefits", exerciseBenefits, "e.g. Reduces stress, improves focus...") { exerciseBenefits = it }
                            Spacer(modifier = Modifier.height(12.dp))
                            CustomInputField("Methods", exerciseMethods, "e.g. Sit upright, relax shoulders...") { exerciseMethods = it }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionTitle("BREATHING PATTERN")
            Spacer(modifier = Modifier.height(8.dp))

            // Breathing Pattern Grid
            OutlinedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PatternControlCol("Inhale", inhale, Color(0xFF5B8FA8)) { inhale = it }
                    Divider(modifier = Modifier.height(140.dp).width(1.dp), color = Color(0xFFF0F0F0))
                    PatternControlCol("Hold", hold1, Color(0xFFF4A261)) { hold1 = it }
                    Divider(modifier = Modifier.height(140.dp).width(1.dp), color = Color(0xFFF0F0F0))
                    PatternControlCol("Exhale", exhale, Color(0xFF2A9D8F)) { exhale = it }
                    Divider(modifier = Modifier.height(140.dp).width(1.dp), color = Color(0xFFF0F0F0))
                    PatternControlCol("Hold", hold2, Color(0xFFE76F51)) { hold2 = it }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Suggested Patterns
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✦ Suggested Patterns:", fontSize = 12.sp, color = TextSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    suggestions.forEach { pattern ->
                        SuggestedChip(
                            label = pattern.label,
                            isSelected = selectedSuggestion == pattern.name,
                            selectedColor = Color(pattern.color)
                        ) {
                            selectedSuggestion = pattern.name
                            inhale = pattern.inhale
                            hold1 = pattern.hold1
                            exhale = pattern.exhale
                            hold2 = pattern.hold2
                            cardColor = pattern.color
                            if (exerciseName.isEmpty()) {
                                exerciseName = pattern.name
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("BREATH CYCLES & DURATION")
            Spacer(modifier = Modifier.height(16.dp))

            // Breath Cycles Arc Control
            BreathCyclesArc(
                cycles = cycles,
                onCyclesChange = { cycles = it },
                duration = formatDuration(cycles, inhale, hold1, exhale, hold2)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionTitle("EXERCISE CARD COLOR")
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val colors = listOf(0xFF9FC4A8, 0xFF9ECDE0, 0xFFE8C4A0, 0xFFD4A8C0, 0xFF4A7C6B, 0xFF5B8FA8)
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(Color(color))
                            .border(
                                width = if (cardColor == color) 3.dp else 1.dp,
                                color = if (cardColor == color) Teal else Color(0xFFE0E0E0),
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                            .clickable { cardColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.height(100.dp)) // padding for FAB
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_sort_by_size), // Placeholder for custom icon
            contentDescription = null,
            tint = Teal,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
    }
}

@Composable
fun PatternControlCol(label: String, value: Float, underlineColor: Color, onValueChange: (Float) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(60.dp)) {
        Text(label, fontSize = 14.sp, color = TextSecondary)
        Box(modifier = Modifier.padding(top = 4.dp).height(2.dp).width(30.dp).background(underlineColor))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "▲",
            fontSize = 18.sp,
            color = Teal,
            modifier = Modifier.clickable { onValueChange(value + 0.5f) }.padding(4.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text("${String.format("%.1f", value)} s", fontSize = 14.sp, color = TextPrimary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "▼",
            fontSize = 18.sp,
            color = Teal,
            modifier = Modifier.clickable { if(value >= 0.5f) onValueChange(value - 0.5f) }.padding(4.dp)
        )
    }
}

@Composable
fun SuggestedChip(label: String, isSelected: Boolean, selectedColor: Color, onClick: () -> Unit) {
    val bgColor = if (isSelected) selectedColor else Color.White
    val textColor = if (isSelected) Color.White else TextSecondary
    val borderColor = if (isSelected) Color.Transparent else Color(0xFFB0B0B0)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontSize = 12.sp, color = textColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BreathCyclesArc(cycles: Int, onCyclesChange: (Int) -> Unit, duration: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "—", 
            fontSize = 24.sp, 
            color = TextSecondary, 
            modifier = Modifier.clickable { if(cycles > 1) onCyclesChange(cycles - 1) }.padding(16.dp)
        )
        
        Box(modifier = Modifier.weight(1f).height(120.dp), contentAlignment = Alignment.BottomCenter) {
            Canvas(modifier = Modifier.fillMaxWidth(0.8f).height(120.dp)) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val radius = canvasWidth / 2
                
                // Draw arc track
                drawArc(
                    color = Color(0xFFE0E0E0),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                    topLeft = Offset(0f, canvasHeight - radius),
                    size = Size(canvasWidth, radius * 2)
                )

                // Calculate thumb position based on cycles (assume max 10 for drawing)
                val maxCycles = 10f
                val fraction = (cycles.toFloat().coerceIn(1f, maxCycles)) / maxCycles
                val angle = 180f + (180f * fraction)
                val angleRad = Math.toRadians(angle.toDouble())
                
                val thumbX = canvasWidth / 2 + radius * cos(angleRad).toFloat()
                val thumbY = canvasHeight + radius * sin(angleRad).toFloat()

                // Draw thumb
                drawCircle(
                    color = Color(0xFF90E0EF),
                    radius = 6.dp.toPx(),
                    center = Offset(thumbX, thumbY)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 8.dp)) {
                Text("$cycles Cycles", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(duration, fontSize = 12.sp, color = TextMuted)
            }
        }

        Text(
            "＋", 
            fontSize = 24.sp, 
            color = TextSecondary, 
            modifier = Modifier.clickable { onCyclesChange(cycles + 1) }.padding(16.dp)
        )
    }
}

fun formatDuration(cycles: Int, inhale: Float, hold1: Float, exhale: Float, hold2: Float): String {
    val totalSeconds = ((inhale + hold1 + exhale + hold2) * cycles).toInt()
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return String.format("00:%02d:%02d", m, s)
}

data class SuggestedPattern(
    val name: String,
    val label: String,
    val inhale: Float,
    val hold1: Float,
    val exhale: Float,
    val hold2: Float,
    val color: Long
)

@Composable
fun CustomInputField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Teal, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = TextMuted) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFE0E0E0),
                unfocusedBorderColor = Color(0xFFF0F0F0),
                focusedContainerColor = Color(0xFFFAFAFA),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
        )
    }
}
