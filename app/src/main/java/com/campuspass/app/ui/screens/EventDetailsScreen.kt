package com.campuspass.app.ui.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.ui.theme.PrimaryBlue
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventTitle: String,
    onBack: () -> Unit,
    onRegister: () -> Unit
) {
    val context = LocalContext.current
    var registered by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // WORKING: Share
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, eventTitle)
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "Check out $eventTitle on CampusPass! Register now."
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share event via"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                    // WORKING: Add to Calendar
                    IconButton(onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, eventTitle)
                            putExtra(CalendarContract.Events.DESCRIPTION, "CampusPass event")
                            putExtra(CalendarContract.Events.EVENT_LOCATION, "Amphitheatre")
                            val beginTime = Calendar.getInstance().apply {
                                set(2026, Calendar.MAY, 24, 18, 0)
                            }
                            val endTime = Calendar.getInstance().apply {
                                set(2026, Calendar.MAY, 24, 23, 0)
                            }
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            showShareDialog = true
                        }
                    }) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF7C3AED))
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.linearGradient(colors = listOf(Color(0xFF7C3AED), Color(0xFF3B5BDB)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(100.dp)
                )
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(eventTitle, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Music · Festival", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(20.dp))
                InfoRow(Icons.Default.CalendarToday, "24 May 2026 · Saturday")
                InfoRow(Icons.Default.AccessTime, "18:00 – 23:00")
                InfoRow(Icons.Default.LocationOn, "Amphitheatre")

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("100 spots", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("42 remaining", fontSize = 13.sp, color = Color(0xFF10B981))
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { 0.58f },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = Color(0xFF10B981),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))
                Text("About this event", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Join us for an unforgettable evening of live music, great vibes, and amazing performances by top local artists.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!registered) {
                            registered = true
                            onRegister()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = !registered
                ) {
                    if (registered) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registered", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Register", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, fontSize = 14.sp)
    }
}