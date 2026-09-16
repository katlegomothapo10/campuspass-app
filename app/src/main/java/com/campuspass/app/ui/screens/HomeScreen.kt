package com.campuspass.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    var userName by remember { mutableStateOf("Student") }

    // Placeholder events
    val events = listOf(
        Event("Campus Music Festival", "24 May 2026", "Amphitheatre", "42/100 spots left"),
        Event("Sports Day 2026", "28 May 2026", "Main Field", "65/200 spots left"),
        Event("Academic Seminar", "02 Jun 2026", "Science Building 1", "30/60 spots left"),
        Event("Cultural Night", "05 Jun 2026", "Student Centre", "72/150 spots left")
    )

    LaunchedEffect(Unit) {
        userName = prefs.getUserName() ?: "Student"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusPass") },
                actions = {
                    IconButton(onClick = onOpenProfile) {
                        Text("👤", fontSize = 20.sp)
                    }
                    IconButton(onClick = onOpenSettings) {
                        Text("⚙", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Welcome, $userName!", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Upcoming Events", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(events) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* TODO: Event details */ }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(event.title, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(event.date, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(event.location, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(event.spots, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

data class Event(
    val title: String,
    val date: String,
    val location: String,
    val spots: String
)