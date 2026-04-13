package com.vital.health.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.vital.health.ui.theme.*

@Composable
fun SettingsScreenContent(
    userName: String,
    userEmail: String,
    userAvatarUrl: String?,
    onSaveProfile: (String, ByteArray?) -> Unit,
    onLogout: () -> Unit,
    onBackup: () -> Unit = {},
    onRestore: () -> Unit = {}
) {
    val initial = if (userName.isNotEmpty()) userName.first().toString().uppercase() else "U"
    var showEditProfile by remember { mutableStateOf(false) }
    var showHealthGoals by remember { mutableStateOf(false) }
    var showNotifications by remember { mutableStateOf(false) }
    var showBackupConfirm by remember { mutableStateOf(false) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(PrimaryBlack),
            contentAlignment = Alignment.Center
        ) {
            if (userAvatarUrl != null) {
                AsyncImage(model = userAvatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Text(initial, color = CreamBg, fontSize = 40.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text(userName, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = TextMain)
        Text(userEmail, fontSize = 14.sp, color = TextMuted)
        
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CreamCard),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                SettingsItem(icon = Icons.Outlined.Person, label = "Personal Information", isToggle = false, hasBorder = true, onClick = { showEditProfile = true })
                SettingsItem(
                    icon = Icons.Outlined.Settings, label = "Dark Mode", isToggle = true, hasBorder = true,
                    checked = isAppDarkMode,
                    onCheckedChange = { isAppDarkMode = it }
                )
                SettingsItem(icon = Icons.Outlined.Info, label = "Health Goals", isToggle = false, hasBorder = true, onClick = { showHealthGoals = true })
                SettingsItem(icon = Icons.Outlined.Notifications, label = "Notifications", isToggle = false, hasBorder = true, onClick = { showNotifications = true })
                SettingsItem(icon = Icons.Outlined.Refresh, label = "Backup Data", isToggle = false, hasBorder = true, onClick = { showBackupConfirm = true })
                SettingsItem(icon = Icons.Outlined.Refresh, label = "Restore Data", isToggle = false, hasBorder = false, onClick = { showRestoreConfirm = true })
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VitalError, contentColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Sign Out", modifier = Modifier.padding(end = 8.dp))
            Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.weight(1f))
        Text("VERSION 2.4.0", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 24.dp))
    }

    if (showEditProfile) {
        EditProfileDialog(currentName = userName, currentAvatarUrl = userAvatarUrl, onDismiss = { showEditProfile = false }, onSave = { name, photoBytes -> onSaveProfile(name, photoBytes); showEditProfile = false })
    }
    if (showHealthGoals) {
        HealthGoalsDialog(onDismiss = { showHealthGoals = false })
    }
    if (showNotifications) {
        NotificationsDialog(onDismiss = { showNotifications = false })
    }
    if (showBackupConfirm) {
        AlertDialog(
            onDismissRequest = { showBackupConfirm = false },
            title = { Text("Backup Data", color = TextMain, fontWeight = FontWeight.Bold) },
            text = { Text("Upload all unsynced health logs to cloud?", color = TextMuted) },
            confirmButton = { Button(onClick = { onBackup(); showBackupConfirm = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Backup", color = CreamBg) } },
            dismissButton = { TextButton(onClick = { showBackupConfirm = false }) { Text("Cancel", color = TextMuted) } },
            containerColor = CreamCard
        )
    }
    if (showRestoreConfirm) {
        AlertDialog(
            onDismissRequest = { showRestoreConfirm = false },
            title = { Text("Restore Data", color = TextMain, fontWeight = FontWeight.Bold) },
            text = { Text("Download all health logs from cloud? This won't delete existing data.", color = TextMuted) },
            confirmButton = { Button(onClick = { onRestore(); showRestoreConfirm = false }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Restore", color = CreamBg) } },
            dismissButton = { TextButton(onClick = { showRestoreConfirm = false }) { Text("Cancel", color = TextMuted) } },
            containerColor = CreamCard
        )
    }
}

@Composable
fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isToggle: Boolean, hasBorder: Boolean,
    checked: Boolean = false, onCheckedChange: ((Boolean) -> Unit)? = null, onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(label, color = TextMain, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        if (isToggle) {
            Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlack))
        } else {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Arrow", tint = TextMuted, modifier = Modifier.size(16.dp))
        }
    }
    if (hasBorder) { HorizontalDivider(color = TanButton, modifier = Modifier.padding(horizontal = 16.dp)) }
}

@Composable
fun EditProfileDialog(currentName: String, currentAvatarUrl: String?, onDismiss: () -> Unit, onSave: (String, ByteArray?) -> Unit) {
    var editName by remember { mutableStateOf(currentName) }
    // Selected image URI and local input stream logic are platform-specific.
    // Simplifying this for commonMain. In a real app, use a multiplatform file picker.
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile", color = TextMain) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(CreamBg), contentAlignment = Alignment.Center) {
                    if (currentAvatarUrl != null) { AsyncImage(model = currentAvatarUrl, contentDescription = "Avatar", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()) }
                    else { val init = if (editName.isNotEmpty()) editName.first().toString().uppercase() else "U"; Text(init, color = TextMain, fontSize = 40.sp, fontWeight = FontWeight.Bold) }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Profile editing limited in shared code", color = TextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(editName.trim(), null)
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun HealthGoalsDialog(onDismiss: () -> Unit) {
    var targetWeight by remember { mutableStateOf("") }
    var targetBP by remember { mutableStateOf("") }
    var dailySteps by remember { mutableStateOf("") }
    var waterIntake by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Health Goals", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = targetWeight, onValueChange = { targetWeight = it }, label = { Text("Target Weight (kg)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = targetBP, onValueChange = { targetBP = it }, label = { Text("Target BP (e.g. 120/80)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = dailySteps, onValueChange = { dailySteps = it }, label = { Text("Daily Step Goal") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(value = waterIntake, onValueChange = { waterIntake = it }, label = { Text("Daily Water (glasses)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = CreamBg) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun NotificationsDialog(onDismiss: () -> Unit) {
    var medReminders by remember { mutableStateOf(true) }
    var vitalsReminder by remember { mutableStateOf(true) }
    var weeklySummary by remember { mutableStateOf(true) }
    var quietHours by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Notifications", color = TextMain, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                NotifToggleRow("Medication Reminders", "Get reminded to take your meds", medReminders) { medReminders = it }
                HorizontalDivider(color = TanButton)
                NotifToggleRow("Vitals Logging Reminder", "Daily reminder to log vitals", vitalsReminder) { vitalsReminder = it }
                HorizontalDivider(color = TanButton)
                NotifToggleRow("Weekly Health Summary", "Receive a weekly digest", weeklySummary) { weeklySummary = it }
                HorizontalDivider(color = TanButton)
                NotifToggleRow("Quiet Hours (10PM–7AM)", "Mute notifications overnight", quietHours) { quietHours = it }
                HorizontalDivider(color = TanButton)
                NotifToggleRow("Sound", "Play notification sounds", soundEnabled) { soundEnabled = it }
            }
        },
        confirmButton = {
            Button(onClick = {
                onDismiss()
            }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlack)) { Text("Save", color = Color.White) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = TextMuted) } },
        containerColor = CreamCard
    )
}

@Composable
fun NotifToggleRow(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextMain, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, color = TextMuted, fontSize = 12.sp)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = PrimaryBlack))
    }
}
