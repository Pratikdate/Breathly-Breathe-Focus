package com.shanacoder.breathly.ui.screens

import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shanacoder.breathly.BreathlyViewModel
import com.shanacoder.breathly.notifications.NotificationScheduler
import com.shanacoder.breathly.ui.theme.*
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BreathlyViewModel,
    onNavigateBack: () -> Unit,
    bottomPadding: androidx.compose.ui.unit.Dp = 0.dp
) {
    val soundsEnabled by viewModel.settingsManager.soundsEnabled.collectAsState(initial = true)
    val followRhythmEnabled by viewModel.settingsManager.followRhythmEnabled.collectAsState(initial = true)
    val hapticsEnabled by viewModel.settingsManager.hapticsEnabled.collectAsState(initial = true)
    val notificationsEnabled by viewModel.settingsManager.notificationsEnabled.collectAsState(initial = false)
    val notificationTime by viewModel.settingsManager.notificationTime.collectAsState(initial = "08:00")
    val dndModeEnabled by viewModel.settingsManager.dndModeEnabled.collectAsState(initial = false)
    
    var showReleaseInfo by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            scope.launch { 
                viewModel.settingsManager.setNotificationsEnabled(true)
                val timeParts = notificationTime.split(":")
                NotificationScheduler.scheduleNotification(context, timeParts[0].toInt(), timeParts[1].toInt())
            }
        }
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        topBar = {
            Column(modifier = Modifier.background(Color(0xFFFAFAFA)).statusBarsPadding()) {
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
                            .clickable { onNavigateBack() }
                    )
                    Text(
                        "Settings",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Audio Experience", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            
            SettingToggle(
                title = "Nature Sounds",
                description = "Play relaxing background sounds during sessions",
                checked = soundsEnabled,
                onCheckedChange = { enabled -> scope.launch { viewModel.settingsManager.setSoundsEnabled(enabled) } }
            )
            
            Divider(color = Color(0xFFE0E0E0))
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { scope.launch { viewModel.settingsManager.setFollowRhythmEnabled(!followRhythmEnabled) } }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Follow Rhythm of Breath", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                    Text("Audio cues to help you follow the breathing rhythm", fontSize = 12.sp, color = Color.Gray)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (followRhythmEnabled) Color(0xFF9FC4A8) else Color(0xFFE0E0E0))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (followRhythmEnabled) "Enabled" else "Disabled",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Sensory", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            
            SettingToggle(
                title = "Haptics",
                description = "Gentle vibrations to guide your breath",
                checked = hapticsEnabled,
                onCheckedChange = { enabled -> scope.launch { viewModel.settingsManager.setHapticsEnabled(enabled) } }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("Reminders", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))
            
            SettingToggle(
                title = "Daily Reminders",
                description = "Get notified to take a breathing break",
                checked = notificationsEnabled,
                onCheckedChange = { enabled -> 
                    if (enabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            notificationLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            scope.launch { 
                                viewModel.settingsManager.setNotificationsEnabled(true)
                                val timeParts = notificationTime.split(":")
                                NotificationScheduler.scheduleNotification(context, timeParts[0].toInt(), timeParts[1].toInt())
                            }
                        }
                    } else {
                        scope.launch { 
                            viewModel.settingsManager.setNotificationsEnabled(false)
                            NotificationScheduler.cancelNotification(context)
                        }
                    }
                }
            )
            
            if (notificationsEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val timeParts = notificationTime.split(":")
                            val hour = timeParts[0].toInt()
                            val minute = timeParts[1].toInt()
                            TimePickerDialog(context, { _, h, m ->
                                scope.launch {
                                    val newTime = String.format("%02d:%02d", h, m)
                                    viewModel.settingsManager.setNotificationTime(newTime)
                                    NotificationScheduler.scheduleNotification(context, h, m)
                                }
                            }, hour, minute, true).show()
                        }
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Reminder Time", fontSize = 16.sp, color = Color(0xFF333333))
                    Text(notificationTime, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4CAF8A))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Focus", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            SettingToggle(
                title = "Do Not Disturb",
                description = "Silence interruptions during sessions",
                checked = dndModeEnabled,
                onCheckedChange = { enabled ->
                    if (enabled && !notificationManager.isNotificationPolicyAccessGranted) {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                        context.startActivity(intent)
                    } else {
                        scope.launch { viewModel.settingsManager.setDndModeEnabled(enabled) }
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text("General", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(bottom = 8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out Breathly, a simple and beautiful breathing app: https://play.google.com/store/apps/details?id=${context.packageName}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Share App", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                    Text("Share Breathly with your friends and family", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Share",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReleaseInfo = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Release Information", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
                    Text("App certificate fingerprints and version info", fontSize = 12.sp, color = Color.Gray)
                }
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Release Info",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(bottomPadding + 32.dp))
        }
    }

    if (showReleaseInfo) {
        AlertDialog(
            onDismissRequest = { showReleaseInfo = false },
            title = { Text("Release Information") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Version: 1.0 (1)", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("MD5 Fingerprint:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("22:81:BA:3F:33:49:34:F6:77:F1:59:98:97:EF:43:C8", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SHA-1 Fingerprint:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("BD:18:54:E6:62:1F:E6:A6:89:DC:9A:F1:26:47:64:6B:17:96:75:41", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SHA-256 Fingerprint:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("DF:33:3E:95:F9:5D:B9:84:34:4A:6F:28:EB:10:0F:10:00:B7:D4:EF:2E:16:53:94:7B:BE:A2:D2:60:9C:59:53", fontSize = 11.sp, color = Color.Gray)
                }
            },
            confirmButton = {
                TextButton(onClick = { showReleaseInfo = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun SettingToggle(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333))
            Text(description, fontSize = 12.sp, color = Color.Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF9FC4A8), checkedTrackColor = Color(0xFFD4E8DA))
        )
    }
}
