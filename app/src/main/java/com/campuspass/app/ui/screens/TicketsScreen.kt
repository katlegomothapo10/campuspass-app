package com.campuspass.app.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.campuspass.app.UserPreferences
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.model.Ticket
import com.campuspass.app.ui.theme.PrimaryBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketsScreen(
    onOpenTicket: (String) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var showQrDialog by remember { mutableStateOf(false) }
    var selectedTicket by remember { mutableStateOf<TicketUiModel?>(null) }

    var tickets by remember {
        mutableStateOf<List<TicketUiModel>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val tabs = listOf(
        "Upcoming",
        "Past",
        "Waiting List"
    )

    /*
     * Load the authenticated user's tickets
     * from the CampusPass API.
     */
    LaunchedEffect(Unit) {

        try {
            val preferences = UserPreferences(context)
            val token = preferences.getToken()

            if (token.isNullOrBlank()) {
                errorMessage = "You are not logged in."
                isLoading = false
                return@LaunchedEffect
            }

            val response = RetrofitInstance.api.getMyTickets(
                "Bearer $token"
            )

            tickets = response.tickets.map {
                it.toUiModel()
            }

            isLoading = false

        } catch (e: Exception) {

            errorMessage =
                e.message ?: "Unable to load your tickets."

            isLoading = false
        }
    }

    /*
     * Filter tickets according to the selected tab.
     */
    val filteredTickets = when (selectedTab) {

        // Upcoming
        0 -> tickets.filter {
            it.status.equals("ACTIVE", ignoreCase = true) ||
                    it.status.equals("UPCOMING", ignoreCase = true)
        }

        // Past
        1 -> tickets.filter {
            it.status.equals("USED", ignoreCase = true) ||
                    it.status.equals("CANCELLED", ignoreCase = true)
        }

        // Waiting List
        // Waiting-list entries are not returned by /tickets/me,
        // so this remains empty for now.
        else -> emptyList()
    }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Tickets",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
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
        ) {

            /*
             * Sync status
             */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(14.dp)
                )

                Text(
                    text = if (isLoading) {
                        " Syncing..."
                    } else {
                        " All synced"
                    },
                    fontSize = 12.sp,
                    color = Color(0xFF10B981)
                )
            }

            /*
             * Tabs
             */
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor =
                    MaterialTheme.colorScheme.background,
                contentColor = PrimaryBlue
            ) {

                tabs.forEachIndexed { index, tab ->

                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        text = {
                            Text(
                                tab,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            /*
             * Screen content
             */
            when {

                /*
                 * Loading
                 */
                isLoading -> {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        CircularProgressIndicator()
                    }
                }

                /*
                 * Error
                 */
                errorMessage != null -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = errorMessage!!,
                            color =
                                MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                }

                /*
                 * No tickets
                 */
                filteredTickets.isEmpty() -> {

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text = if (selectedTab == 2) {
                                "No waiting list entries."
                            } else {
                                "No tickets found."
                            },
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

                /*
                 * Display tickets
                 */
                else -> {

                    LazyColumn(
                        contentPadding =
                            PaddingValues(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {

                        items(filteredTickets) { ticket ->

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
        }
    }

    /*
     * QR dialog
     */
    if (showQrDialog && selectedTicket != null) {

        TicketQrDialog(
            ticket = selectedTicket!!,
            onDismiss = {
                showQrDialog = false
            }
        )
    }
}


/*
 * Ticket QR dialog
 */
@Composable
fun TicketQrDialog(
    ticket: TicketUiModel,
    onDismiss: () -> Unit
) {

    val context = LocalContext.current

    var qrBitmap by remember {
        mutableStateOf<android.graphics.Bitmap?>(null)
    }

    var isLoadingQr by remember {
        mutableStateOf(true)
    }

    var qrError by remember {
        mutableStateOf<String?>(null)
    }

    /*
     * Retrieve the QR code from the backend.
     */
    LaunchedEffect(ticket.ticketId) {

        try {

            val preferences =
                UserPreferences(context)

            val token =
                preferences.getToken()

            if (token.isNullOrBlank()) {

                qrError = "You are not logged in."
                isLoadingQr = false

                return@LaunchedEffect
            }

            val response =
                RetrofitInstance.api.getTicketQr(
                    "Bearer $token",
                    ticket.ticketId
                )

            /*
             * Backend returns:
             *
             * data:image/png;base64,...
             */
            val dataUrl =
                response.qrDataUrl

            val base64Data =
                dataUrl.substringAfter(
                    "base64,",
                    ""
                )

            if (base64Data.isBlank()) {

                qrError =
                    "Invalid QR code data."

                isLoadingQr = false

                return@LaunchedEffect
            }

            val imageBytes =
                Base64.decode(
                    base64Data,
                    Base64.DEFAULT
                )

            qrBitmap =
                BitmapFactory.decodeByteArray(
                    imageBytes,
                    0,
                    imageBytes.size
                )

            isLoadingQr = false

        } catch (e: Exception) {

            qrError =
                e.message ?: "Unable to load QR code."

            isLoadingQr = false
        }
    }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Text(
                ticket.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },

        text = {

            Column(
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                /*
                 * QR image container
                 */
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .background(
                            Color.White,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    when {

                        /*
                         * Loading QR
                         */
                        isLoadingQr -> {

                            CircularProgressIndicator()
                        }

                        /*
                         * QR error
                         */
                        qrError != null -> {

                            Text(
                                text = qrError!!,
                                color =
                                    MaterialTheme.colorScheme.error,
                                fontSize = 12.sp
                            )
                        }

                        /*
                         * Display actual QR
                         */
                        qrBitmap != null -> {

                            Image(
                                bitmap =
                                    qrBitmap!!.asImageBitmap(),
                                contentDescription =
                                    "Ticket QR code",
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(8.dp)
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    "Scan for Entry",
                    fontSize = 13.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    ticket.date,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    ticket.location,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("Close")
            }
        }
    )
}


/*
 * Ticket card
 */
@Composable
fun TicketCard(
    ticket: TicketUiModel,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 3.dp
            )
    ) {

        Row(
            modifier =
                Modifier.padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            /*
             * Ticket icon
             */
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        ticket.statusColor
                            .copy(alpha = 0.15f)
                    ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint =
                        ticket.statusColor,
                    modifier =
                        Modifier.size(32.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    ticket.title,
                    fontSize = 15.sp,
                    fontWeight =
                        FontWeight.SemiBold
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    ticket.date,
                    fontSize = 12.sp,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        modifier =
                            Modifier.size(12.dp)
                    )

                    Text(
                        " ${ticket.location}",
                        fontSize = 12.sp,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Surface(
                    shape =
                        RoundedCornerShape(6.dp),

                    color =
                        ticket.statusColor
                            .copy(alpha = 0.15f)
                ) {

                    Text(
                        ticket.status,
                        fontSize = 10.sp,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            ticket.statusColor,

                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                    )
                }
            }

            /*
             * QR icon
             */
            Icon(
                Icons.Default.QrCode,
                contentDescription =
                    "Show QR",
                tint = PrimaryBlue,
                modifier =
                    Modifier.size(28.dp)
            )
        }
    }
}


/*
 * UI-specific ticket model.
 *
 * This is deliberately separate from
 * data.model.Ticket, which represents
 * the backend API response.
 */
data class TicketUiModel(
    val ticketId: Int,
    val title: String,
    val date: String,
    val location: String,
    val status: String,
    val statusColor: Color
)


/*
 * Convert the backend Ticket model
 * into the model used by the Compose UI.
 */
private fun Ticket.toUiModel(): TicketUiModel {

    val displayDate =
        buildString {

            if (!eventDate.isNullOrBlank()) {
                append(eventDate)
            }

            if (!eventTime.isNullOrBlank()) {

                if (isNotEmpty()) {
                    append(" · ")
                }

                append(eventTime)
            }

            if (isEmpty()) {
                append("Date unavailable")
            }
        }

    val displayLocation =
        eventLocation ?: "Location unavailable"

    val displayStatus =
        status.uppercase()

    val statusColor =
        when {

            status.equals(
                "used",
                ignoreCase = true
            ) -> Color(0xFF6B7280)

            status.equals(
                "cancelled",
                ignoreCase = true
            ) -> Color(0xFFEF4444)

            else -> Color(0xFF3B5BDB)
        }

    return TicketUiModel(
        ticketId = ticketId,
        title = eventTitle ?: "Campus Event",
        date = displayDate,
        location = displayLocation,
        status = displayStatus,
        statusColor = statusColor
    )
}