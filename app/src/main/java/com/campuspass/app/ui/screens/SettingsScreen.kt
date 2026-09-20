package com.campuspass.app.ui.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOpenChangePassword: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenTermsOfService: () -> Unit = {},
    onOpenHelp: () -> Unit = {}
) {
    val context = LocalContext.current

    var selectedLanguage by remember { mutableStateOf("English") }
    var eventReminders by remember { mutableStateOf(true) }
    var ticketUpdates by remember { mutableStateOf(true) }
    var promotions by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }

    val languages = listOf("English", "isiZulu", "Afrikaans")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("General", fontSize = 13.sp, color = Color(0xFF3B5BDB), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = !languageExpanded }
            ) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang) },
                            onClick = {
                                selectedLanguage = lang
                                languageExpanded = false
                                applyLanguage(context, lang)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Notifications", fontSize = 13.sp, color = Color(0xFF3B5BDB), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow("Event Reminders", eventReminders) { eventReminders = it }
            ToggleRow("Ticket Updates", ticketUpdates) { ticketUpdates = it }
            ToggleRow("Promotions & News", promotions) { promotions = it }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Security", fontSize = 13.sp, color = Color(0xFF3B5BDB), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow("Biometric Authentication", biometricEnabled) { biometricEnabled = it }

            OutlinedButton(
                onClick = onOpenChangePassword,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Legal", fontSize = 13.sp, color = Color(0xFF3B5BDB), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenPrivacyPolicy,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.PrivacyTip, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Privacy Policy")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenTermsOfService,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Terms of Service")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onOpenHelp,
                modifier = Modifier.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Help, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Help & Support")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
            ) {
                Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "CampusPass v1.0.0",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ToggleRow(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 14.sp)
            Switch(checked = checked, onCheckedChange = onToggle)
        }
    }
}

private fun applyLanguage(context: Context, languageName: String) {
    val code = when (languageName) {
        "isiZulu" -> "zu"
        "Afrikaans" -> "af"
        else -> "en"
    }
    val locale = java.util.Locale(code)
    java.util.Locale.setDefault(locale)
    val config = android.content.res.Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
    (context as? Activity)?.recreate()
}