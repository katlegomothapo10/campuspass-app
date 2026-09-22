package com.campuspass.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.UserPreferences
import com.campuspass.app.ui.theme.AccentPurple
import com.campuspass.app.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    onOpenEditProfile: () -> Unit = {},
    onOpenMyEvents: () -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    var userName by remember {
        mutableStateOf("Student")
    }

    var userEmail by remember {
        mutableStateOf("student@campus.ac.za")
    }

    var biometricEnabled by remember {
        mutableStateOf(false)
    }

    var showLogoutDialog by remember {
        mutableStateOf(false)
    }

    var isLoggingOut by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        userName =
            prefs.getUserName()
                ?: "Student"

        userEmail =
            prefs.getUserEmail()
                ?: "student@campus.ac.za"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },

                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = onOpenSettings
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.background
                    )
            )
        },

        containerColor =
            MaterialTheme.colorScheme.background

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(
                    rememberScrollState()
                ),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            // =========================
            // PROFILE IMAGE
            // =========================

            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PrimaryBlue,
                                AccentPurple
                            )
                        )
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text =
                        userName
                            .take(1)
                            .uppercase(),

                    fontSize = 48.sp,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme.colorScheme.onPrimary
                )
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            // =========================
            // USER DETAILS
            // =========================

            Text(
                text = userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = userEmail,
                fontSize = 13.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "CampusPass Account",
                fontSize = 12.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            // =========================
            // PROFILE STATISTICS
            // =========================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {

                StatCard(
                    "12",
                    "Events Attended",
                    PrimaryBlue
                )

                StatCard(
                    "4",
                    "Events Organized",
                    AccentPurple
                )

                StatCard(
                    "8",
                    "Tickets Saved",
                    Color(0xFF10B981)
                )
            }


            Spacer(
                modifier = Modifier.height(32.dp)
            )


            // =========================
            // PROFILE MENU
            // =========================

            ProfileMenuItem(
                icon = Icons.Default.Edit,
                title = "Edit Profile",
                onClick = onOpenEditProfile
            )

            ProfileMenuItem(
                icon = Icons.Default.Event,
                title = "My Events",
                badge = "Organizer",
                badgeColor = PrimaryBlue,
                onClick = onOpenMyEvents
            )

            ProfileMenuItem(
                icon = Icons.Default.Settings,
                title = "Settings",
                onClick = onOpenSettings
            )

            ProfileMenuItem(
                icon = Icons.Default.Fingerprint,
                title = "Biometric Login",

                trailing = {
                    Switch(
                        checked =
                            biometricEnabled,

                        onCheckedChange = {
                            biometricEnabled = it
                        }
                    )
                },

                onClick = {
                    biometricEnabled =
                        !biometricEnabled
                }
            )

            ProfileMenuItem(
                icon = Icons.Default.Help,
                title = "Help & Support",
                onClick = { }
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            // =========================
            // LOGOUT
            // =========================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),

                shape =
                    RoundedCornerShape(14.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 2.dp
                    ),

                onClick = {
                    showLogoutDialog = true
                }
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )

                    Spacer(
                        modifier = Modifier.width(12.dp)
                    )

                    Text(
                        text = "Logout",
                        fontSize = 15.sp,
                        color = Color(0xFFEF4444),
                        fontWeight =
                            FontWeight.Medium,
                        modifier =
                            Modifier.weight(1f)
                    )

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
    }


    // =========================
    // LOGOUT CONFIRMATION
    // =========================

    if (showLogoutDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!isLoggingOut) {
                    showLogoutDialog = false
                }
            },

            icon = {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null,
                    tint = Color(0xFFEF4444)
                )
            },

            title = {
                Text(
                    text = "Log out?"
                )
            },

            text = {
                Text(
                    text =
                        "You'll need to sign in again to access your CampusPass account."
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        if (!isLoggingOut) {

                            isLoggingOut = true

                            coroutineScope.launch {

                                // Clear JWT, name and email
                                // from DataStore.
                                prefs.clear()

                                // Return to the app's
                                // unauthenticated/login flow.
                                onLogout()
                            }
                        }
                    },

                    enabled = !isLoggingOut,

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFFEF4444)
                        )
                ) {

                    if (isLoggingOut) {

                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(18.dp),

                            strokeWidth = 2.dp,

                            color = Color.White
                        )

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )
                    }

                    Text(
                        text =
                            if (isLoggingOut)
                                "Logging out..."
                            else
                                "Logout"
                    )
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    },

                    enabled =
                        !isLoggingOut
                ) {

                    Text("Cancel")
                }
            }
        )
    }
}


// =====================================================
// STAT CARD
// =====================================================

@Composable
fun StatCard(
    value: String,
    label: String,
    color: Color
) {

    Card(
        modifier =
            Modifier.width(105.dp),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = label,
                fontSize = 10.sp,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


// =====================================================
// PROFILE MENU ITEM
// =====================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    badgeColor: Color = PrimaryBlue,
    trailing: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 4.dp
            ),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            ),

        onClick = onClick
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier =
                    Modifier.size(22.dp)
            )

            Spacer(
                modifier =
                    Modifier.width(16.dp)
            )

            Text(
                text = title,
                fontSize = 15.sp,
                modifier =
                    Modifier.weight(1f)
            )


            if (badge != null) {

                Surface(
                    shape =
                        RoundedCornerShape(6.dp),

                    color =
                        badgeColor.copy(
                            alpha = 0.15f
                        )
                ) {

                    Text(
                        text = badge,

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color = badgeColor,

                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                    )
                }
            }


            if (trailing != null) {

                trailing()

            } else {

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.size(20.dp)
                )
            }
        }
    }
}