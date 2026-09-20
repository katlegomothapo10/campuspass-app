package com.campuspass.app.ui.screens
import com.campuspass.app.ui.screens.ProfileScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.campuspass.app.ui.theme.PrimaryBlue

@Composable
fun MainScreen(
    onOpenSettings: () -> Unit,
    onOpenEventDetails: (String) -> Unit,
    onOpenEditProfile: () -> Unit = {},
    onOpenMyEvents: () -> Unit = {},
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Tickets") },
                    label = { Text("Tickets") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onOpenSettings = onOpenSettings,
                    onOpenProfile = { selectedTab = 2 },
                    onLogout = onLogout,
                    onOpenEventDetails = onOpenEventDetails
                )
                1 -> TicketsScreen()
                2 -> ProfileScreen(
                    onBack = { selectedTab = 0 },
                    onOpenSettings = onOpenSettings,
                    onLogout = onLogout,
                    onOpenEditProfile = onOpenEditProfile,
                    onOpenMyEvents = onOpenMyEvents
                )
            }
        }
    }
}