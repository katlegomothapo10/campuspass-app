package com.campuspass.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    onOpenTicket: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showQrDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<Ticket?>(null) }

    val tabs = listOf("Upcoming", "Past", "Waiting List")

    val tickets = listOf(
        Ticket("Campus Music Festival", "24 May 2026 · 18:00", "Amphitheatre", "UPCOMING", Color(0xFF10B981)),
        Ticket("Sports Day 2026", "28 May 2026 · 09:00", "Main Field", "USED", Color(0xFF6B7280)),
        Ticket("Academic Seminar", "02 Jun 2026 · 14:00", "Science Building 1", "ACTIVE", Color(0xFF3B5BDB)),
        Ticket("Cultural Night", "05 Jun 2026 · 18:00", "Student Centre", "ACTIVE", Color(0xFF3B5BDB))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tickets", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )
                Text(" All synced", fontSize = 12.sp, color = Color(0xFF10B981))
            }

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = PrimaryBlue
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tab, fontSize = 13.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tickets) { ticket ->
                    TicketCard(
                        ticket = ticket,
                        onClick = {
                            selectedTicket = ticket
                            showQrDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showQrDialog && selectedTicket != null) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = {
                Text(
                    selectedTicket!!.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.QrCode,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(160.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Scan for Entry", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        selectedTicket!!.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        selectedTicket!!.location,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun TicketCard(ticket: Ticket, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ticket.statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = ticket.statusColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(ticket.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    ticket.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        " ${ticket.location}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ticket.statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        ticket.status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ticket.statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Icon(
                Icons.Default.QrCode,
                contentDescription = "Show QR",
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

data class Ticket(
    val title: String,
    val date: String,
    val location: String,
    val status: String,
    val statusColor: Color
)