package com.campuspass.app.ui.screens

import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.UserPreferences
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.model.Event
import com.campuspass.app.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale

// =========================================================
// JWT USER INFO
// =========================================================

private data class JwtUserInfo(
    val userId: Int,
    val role: String
)


// =========================================================
// MY EVENTS SCREEN
// =========================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    onBack: () -> Unit,
    onScanTickets: () -> Unit
) {

    val context = LocalContext.current

    val prefs =
        remember {
            UserPreferences(context)
        }

    val coroutineScope =
        rememberCoroutineScope()


    var events by remember {
        mutableStateOf<List<Event>>(emptyList())
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isOrganizer by remember {
        mutableStateOf(false)
    }


    // =====================================================
    // LOAD ORGANIZER EVENTS
    // =====================================================

    fun loadMyEvents() {

        coroutineScope.launch {

            isLoading = true
            errorMessage = null

            try {

                // -----------------------------------------
                // GET SAVED JWT
                // -----------------------------------------

                val token =
                    prefs.getToken()

                if (token.isNullOrBlank()) {

                    errorMessage =
                        "Your session has expired. Please log in again."

                    return@launch
                }


                // -----------------------------------------
                // READ USER ID + ROLE FROM JWT
                // -----------------------------------------

                val jwtUser =
                    decodeJwtUser(token)

                if (jwtUser == null) {

                    errorMessage =
                        "Unable to read your account information."

                    return@launch
                }


                // -----------------------------------------
                // CHECK ORGANIZER ROLE
                // -----------------------------------------

                isOrganizer =
                    jwtUser.role.equals(
                        "organizer",
                        ignoreCase = true
                    ) ||
                            jwtUser.role.equals(
                                "admin",
                                ignoreCase = true
                            )


                if (!isOrganizer) {

                    events =
                        emptyList()

                    return@launch
                }


                // -----------------------------------------
                // GET ALL EVENTS
                // -----------------------------------------

                val eventResponse =
                    RetrofitInstance.api
                        .getEvents()


                // -----------------------------------------
                // FILTER ORGANIZER EVENTS
                // -----------------------------------------

                events =
                    if (
                        jwtUser.role.equals(
                            "admin",
                            ignoreCase = true
                        )
                    ) {

                        // Admin can see all events.
                        eventResponse.events

                    } else {

                        // Organizer only sees their events.
                        eventResponse.events.filter { event ->

                            event.organizerUserId ==
                                    jwtUser.userId
                        }
                    }


            } catch (e: HttpException) {

                errorMessage =
                    when (e.code()) {

                        401 ->
                            "Your session has expired. Please log in again."

                        403 ->
                            "You do not have permission to view organizer events."

                        else ->
                            "Unable to load your events."
                    }


            } catch (e: Exception) {

                errorMessage =
                    e.message
                        ?: "Unable to load your events."

            } finally {

                isLoading = false
            }
        }
    }


    // =====================================================
    // LOAD WHEN SCREEN OPENS
    // =====================================================

    LaunchedEffect(Unit) {
        loadMyEvents()
    }


    // =====================================================
    // SCREEN
    // =====================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "My Events",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults
                        .topAppBarColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .background
                        )
            )
        },

        containerColor =
            MaterialTheme.colorScheme.background

    ) { padding ->


        when {


            // =================================================
            // LOADING
            // =================================================

            isLoading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),

                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color = PrimaryBlue
                    )
                }
            }


            // =================================================
            // ERROR
            // =================================================

            errorMessage != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text =
                            errorMessage!!,

                        fontSize = 15.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .error
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    OutlinedButton(
                        onClick = {
                            loadMyEvents()
                        }
                    ) {

                        Text("Try Again")
                    }
                }
            }


            // =================================================
            // NOT AN ORGANIZER
            // =================================================

            !isOrganizer -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.QrCodeScanner,

                        contentDescription = null,

                        modifier =
                            Modifier.size(64.dp),

                        tint =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    Text(
                        text =
                            "Organizer Access Required",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            "This area is available to event organizers and administrators.",

                        fontSize = 14.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }


            // =================================================
            // NO EVENTS
            // =================================================

            events.isEmpty() -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),

                    horizontalAlignment =
                        Alignment.CenterHorizontally,

                    verticalArrangement =
                        Arrangement.Center
                ) {

                    Text(
                        text =
                            "No events yet",

                        fontSize = 20.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )


                    Text(
                        text =
                            "Events you organize will appear here.",

                        fontSize = 14.sp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }


            // =================================================
            // ORGANIZER EVENTS
            // =================================================

            else -> {

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),

                    contentPadding =
                        PaddingValues(16.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {


                    item {

                        Text(
                            text =
                                "Events you're organizing",

                            fontSize = 13.sp,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )


                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )
                    }


                    items(
                        items = events,

                        key = { event ->
                            event.eventId
                        }
                    ) { event ->


                        OrganizerEventCard(

                            event = event,

                            onScanTickets =
                                onScanTickets
                        )
                    }
                }
            }
        }
    }
}


// =========================================================
// ORGANIZER EVENT CARD
// =========================================================

@Composable
private fun OrganizerEventCard(
    event: Event,
    onScanTickets: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(14.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {


            Text(
                text =
                    event.title,

                fontSize = 16.sp,

                fontWeight =
                    FontWeight.SemiBold
            )


            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )


            Text(
                text =
                    "${formatOrganizerEventDate(event.date)} · ${event.location}",

                fontSize = 12.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                Text(
                    text =
                        "${event.registeredCount}/${event.capacity} registered",

                    fontSize = 12.sp,

                    color =
                        PrimaryBlue,

                    fontWeight =
                        FontWeight.Medium
                )


                Surface(
                    shape =
                        RoundedCornerShape(6.dp),

                    color =
                        getStatusColor(
                            event.status
                        ).copy(
                            alpha = 0.15f
                        )
                ) {

                    Text(
                        text =
                            event.status
                                ?.uppercase()
                                ?: "UPCOMING",

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            getStatusColor(
                                event.status
                            ),

                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(16.dp)
            )


            // =================================================
            // SCAN BUTTON
            // =================================================

            Button(
                onClick =
                    onScanTickets,

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(10.dp)
            ) {


                Icon(
                    imageVector =
                        Icons.Default.QrCodeScanner,

                    contentDescription = null,

                    modifier =
                        Modifier.size(19.dp)
                )


                Spacer(
                    modifier =
                        Modifier.width(8.dp)
                )


                Text(
                    text =
                        "Scan Tickets",

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}


// =========================================================
// JWT DECODER
// =========================================================

private fun decodeJwtUser(
    token: String
): JwtUserInfo? {

    return try {

        /*
         * JWT format:
         *
         * header.payload.signature
         */

        val parts =
            token.split(".")


        if (parts.size != 3) {
            return null
        }


        // The second JWT section contains the payload.
        val payload =
            parts[1]


        val decodedBytes =
            Base64.decode(
                payload,
                Base64.URL_SAFE or
                        Base64.NO_WRAP or
                        Base64.NO_PADDING
            )


        val decodedPayload =
            String(
                decodedBytes,
                Charsets.UTF_8
            )


        val json =
            JSONObject(
                decodedPayload
            )


        val userId =
            json.optInt(
                "userId",
                -1
            )


        val role =
            json.optString(
                "role",
                ""
            )


        if (
            userId <= 0 ||
            role.isBlank()
        ) {

            null

        } else {

            JwtUserInfo(
                userId = userId,
                role = role
            )
        }


    } catch (_: Exception) {

        null
    }
}


// =========================================================
// DATE FORMATTER
// =========================================================

private fun formatOrganizerEventDate(
    date: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )


        val output =
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )


        val parsed =
            input.parse(date)


        if (parsed != null) {

            output.format(parsed)

        } else {

            date
        }


    } catch (_: Exception) {

        date
    }
}


// =========================================================
// STATUS COLOUR
// =========================================================

private fun getStatusColor(
    status: String?
): Color {

    return when (
        status?.lowercase()
    ) {

        "cancelled" ->
            Color(0xFFEF4444)

        "completed" ->
            Color(0xFF64748B)

        else ->
            Color(0xFF10B981)
    }
}