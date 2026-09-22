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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuspass.app.UserPreferences
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.model.Event
import com.campuspass.app.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    eventId: Int,
    onBack: () -> Unit,
    onRegister: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val prefs = remember { UserPreferences(context) }

    var event by remember {
        mutableStateOf<Event?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var isRegistering by remember {
        mutableStateOf(false)
    }

    var registered by remember {
        mutableStateOf(false)
    }

    var registrationMessage by remember {
        mutableStateOf<String?>(null)
    }

    var registrationError by remember {
        mutableStateOf<String?>(null)
    }

    // =========================
    // LOAD EVENT
    // =========================

    fun loadEvent() {
        if (eventId <= 0) {
            isLoading = false
            errorMessage = "Invalid event ID."
            return
        }

        coroutineScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response =
                    RetrofitInstance.api.getEvent(eventId)

                event = response.event

            } catch (e: Exception) {
                errorMessage =
                    e.message ?: "Unable to load event."

            } finally {
                isLoading = false
            }
        }
    }

    // =========================
    // REGISTER
    // =========================

    fun registerForEvent() {
        if (eventId <= 0) {
            registrationError = "Invalid event ID."
            return
        }

        coroutineScope.launch {
            isRegistering = true
            registrationError = null
            registrationMessage = null

            try {
                val token = prefs.getToken()

                if (token.isNullOrBlank()) {
                    registrationError =
                        "Your session has expired. Please log in again."

                    return@launch
                }

                RetrofitInstance.api.registerForEvent(
                    token = "Bearer $token",
                    eventId = eventId
                )

                registered = true

                registrationMessage =
                    "Registration successful! Your ticket is ready."

                // Refresh capacity after successful registration.
                try {
                    val refreshed =
                        RetrofitInstance.api.getEvent(eventId)

                    event = refreshed.event
                } catch (_: Exception) {
                    // Registration already succeeded.
                }

                onRegister(eventId)

            } catch (e: HttpException) {

                registrationError =
                    when (e.code()) {
                        400 ->
                            "Unable to register for this event."

                        401 ->
                            "Your session has expired. Please log in again."

                        403 ->
                            "You are not allowed to register for this event."

                        404 ->
                            "This event could not be found."

                        409 ->
                            "You are already registered for this event."

                        else ->
                            "Registration failed. Please try again."
                    }

            } catch (e: Exception) {

                registrationError =
                    e.message
                        ?: "Registration failed. Please try again."

            } finally {
                isRegistering = false
            }
        }
    }

    LaunchedEffect(eventId) {
        loadEvent()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = event?.title ?: "Event Details",
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (event != null) {

                        // SHARE EVENT
                        IconButton(
                            onClick = {
                                val currentEvent =
                                    event ?: return@IconButton

                                val shareIntent =
                                    Intent(
                                        Intent.ACTION_SEND
                                    ).apply {
                                        type = "text/plain"

                                        putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            currentEvent.title
                                        )

                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            buildString {
                                                append(
                                                    "Check out ${currentEvent.title} on CampusPass!\n\n"
                                                )
                                                append(
                                                    "Date: ${formatEventDate(currentEvent.date)}\n"
                                                )
                                                append(
                                                    "Time: ${currentEvent.time}\n"
                                                )
                                                append(
                                                    "Location: ${currentEvent.location}\n\n"
                                                )
                                                append(
                                                    "Register now on CampusPass."
                                                )
                                            }
                                        )
                                    }

                                context.startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Share event via"
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }

                        // ADD TO CALENDAR
                        IconButton(
                            onClick = {
                                val currentEvent =
                                    event ?: return@IconButton

                                val beginTime =
                                    createCalendarTime(
                                        currentEvent.date,
                                        currentEvent.time
                                    )

                                val intent =
                                    Intent(
                                        Intent.ACTION_INSERT
                                    ).apply {
                                        data =
                                            CalendarContract.Events.CONTENT_URI

                                        putExtra(
                                            CalendarContract.Events.TITLE,
                                            currentEvent.title
                                        )

                                        putExtra(
                                            CalendarContract.Events.DESCRIPTION,
                                            currentEvent.description
                                                ?: "CampusPass event"
                                        )

                                        putExtra(
                                            CalendarContract.Events.EVENT_LOCATION,
                                            currentEvent.location
                                        )

                                        if (beginTime != null) {
                                            putExtra(
                                                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                beginTime
                                            )

                                            putExtra(
                                                CalendarContract.EXTRA_EVENT_END_TIME,
                                                beginTime +
                                                        (2 * 60 * 60 * 1000)
                                            )
                                        }
                                    }

                                try {
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    // Calendar app unavailable.
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Add to calendar",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            event?.let {
                                getEventColor(it.category)
                            } ?: PrimaryBlue,

                        titleContentColor = Color.White
                    )
            )
        },
        containerColor =
            MaterialTheme.colorScheme.background
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Loading event...",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(56.dp),
                            tint =
                                MaterialTheme.colorScheme.error
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Couldn't load event",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                errorMessage ?: "Unknown error",
                            fontSize = 13.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        Button(
                            onClick = {
                                loadEvent()
                            }
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Refresh,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text("Try Again")
                        }
                    }
                }
            }

            event != null -> {
                val currentEvent = event!!

                val spotsLeft =
                    (
                            currentEvent.capacity -
                                    currentEvent.registeredCount
                            ).coerceAtLeast(0)

                val registrationProgress =
                    if (currentEvent.capacity > 0) {
                        (
                                currentEvent.registeredCount.toFloat() /
                                        currentEvent.capacity.toFloat()
                                ).coerceIn(0f, 1f)
                    } else {
                        0f
                    }

                val eventColor =
                    getEventColor(
                        currentEvent.category
                    )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(
                            rememberScrollState()
                        )
                ) {

                    // =========================
                    // HERO
                    // =========================

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        eventColor,
                                        eventColor.copy(
                                            alpha = 0.65f
                                        )
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector =
                                getEventIcon(
                                    currentEvent.category
                                ),
                            contentDescription = null,
                            tint =
                                Color.White.copy(
                                    alpha = 0.35f
                                ),
                            modifier = Modifier.size(100.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = currentEvent.title,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                currentEvent.category
                                    .replaceFirstChar {
                                        if (it.isLowerCase()) {
                                            it.titlecase(
                                                Locale.getDefault()
                                            )
                                        } else {
                                            it.toString()
                                        }
                                    },
                            fontSize = 13.sp,
                            color = eventColor,
                            fontWeight =
                                FontWeight.SemiBold
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        // EVENT INFORMATION

                        InfoRow(
                            icon =
                                Icons.Default.CalendarToday,
                            text =
                                formatEventDate(
                                    currentEvent.date
                                )
                        )

                        InfoRow(
                            icon =
                                Icons.Default.AccessTime,
                            text = currentEvent.time
                        )

                        InfoRow(
                            icon =
                                Icons.Default.LocationOn,
                            text = currentEvent.location
                        )

                        Spacer(
                            modifier = Modifier.height(20.dp)
                        )

                        // CAPACITY

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {
                            Text(
                                text =
                                    "${currentEvent.capacity} spots",
                                fontSize = 13.sp,
                                fontWeight =
                                    FontWeight.SemiBold
                            )

                            Text(
                                text = "$spotsLeft remaining",
                                fontSize = 13.sp,
                                color =
                                    if (spotsLeft == 0)
                                        Color(0xFFEF4444)
                                    else
                                        Color(0xFF10B981),
                                fontWeight =
                                    FontWeight.Medium
                            )
                        }

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        LinearProgressIndicator(
                            progress = {
                                registrationProgress
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color =
                                if (spotsLeft == 0)
                                    Color(0xFFEF4444)
                                else
                                    Color(0xFF10B981),
                            trackColor =
                                MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // DESCRIPTION

                        Text(
                            text = "About this event",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text =
                                currentEvent.description
                                    ?: "No description has been provided for this event.",
                            fontSize = 14.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(
                            modifier = Modifier.height(24.dp)
                        )

                        // REGISTRATION SUCCESS

                        registrationMessage?.let { message ->
                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(12.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            Color(0xFF10B981)
                                                .copy(
                                                    alpha = 0.12f
                                                )
                                    )
                            ) {
                                Row(
                                    modifier =
                                        Modifier.padding(14.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint =
                                            Color(0xFF10B981)
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(10.dp)
                                    )

                                    Text(
                                        text = message,
                                        fontSize = 13.sp,
                                        color =
                                            Color(0xFF10B981),
                                        fontWeight =
                                            FontWeight.Medium
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )
                        }

                        // REGISTRATION ERROR

                        registrationError?.let { message ->
                            Card(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                shape =
                                    RoundedCornerShape(12.dp),
                                colors =
                                    CardDefaults.cardColors(
                                        containerColor =
                                            MaterialTheme.colorScheme.errorContainer
                                    )
                            ) {
                                Row(
                                    modifier =
                                        Modifier.padding(14.dp),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector =
                                            Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint =
                                            MaterialTheme.colorScheme.error
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(10.dp)
                                    )

                                    Text(
                                        text = message,
                                        fontSize = 13.sp,
                                        color =
                                            MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )
                        }

                        // REGISTER BUTTON

                        Button(
                            onClick = {
                                registerForEvent()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape =
                                RoundedCornerShape(14.dp),
                            enabled =
                                !isRegistering &&
                                        !registered &&
                                        spotsLeft > 0 &&
                                        !currentEvent.status.equals(
                                            "cancelled",
                                            ignoreCase = true
                                        )
                        ) {
                            when {
                                isRegistering -> {
                                    CircularProgressIndicator(
                                        modifier =
                                            Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = Color.White
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text("Registering...")
                                }

                                registered -> {
                                    Icon(
                                        imageVector =
                                            Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text(
                                        text = "Registered",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }

                                spotsLeft == 0 -> {
                                    Text(
                                        text = "Event Full",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }

                                currentEvent.status.equals(
                                    "cancelled",
                                    ignoreCase = true
                                ) -> {
                                    Text(
                                        text = "Event Cancelled",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }

                                else -> {
                                    Icon(
                                        imageVector =
                                            Icons.Default.ConfirmationNumber,
                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(8.dp)
                                    )

                                    Text(
                                        text = "Register",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }
                }
            }
        }
    }
}


// =========================
// INFORMATION ROW
// =========================

@Composable
fun InfoRow(
    icon: ImageVector,
    text: String
) {
    Row(
        modifier = Modifier.padding(
            vertical = 6.dp
        ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = text,
            fontSize = 14.sp
        )
    }
}


// =========================
// EVENT COLOUR
// =========================

private fun getEventColor(
    category: String
): Color {
    return when (category.lowercase()) {
        "academic" ->
            Color(0xFF3B5BDB)

        "social" ->
            Color(0xFF7C3AED)

        "sports" ->
            Color(0xFF10B981)

        "cultural" ->
            Color(0xFFF59E0B)

        else ->
            Color(0xFF06B6D4)
    }
}


// =========================
// EVENT ICON
// =========================

private fun getEventIcon(
    category: String
): ImageVector {
    return when (category.lowercase()) {
        "academic" ->
            Icons.Default.School

        "social" ->
            Icons.Default.People

        "sports" ->
            Icons.Default.SportsBasketball

        "cultural" ->
            Icons.Default.TheaterComedy

        else ->
            Icons.Default.Event
    }
}


// =========================
// DATE FORMATTING
// =========================

private fun formatEventDate(
    date: String
): String {
    return try {
        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val parsedDate =
            input.parse(date)

        val output =
            SimpleDateFormat(
                "dd MMMM yyyy · EEEE",
                Locale.getDefault()
            )

        if (parsedDate != null) {
            output.format(parsedDate)
        } else {
            date
        }

    } catch (_: Exception) {
        date
    }
}


// =========================
// CALENDAR DATE/TIME
// =========================

private fun createCalendarTime(
    date: String,
    time: String
): Long? {
    return try {
        val formatter =
            SimpleDateFormat(
                "yyyy-MM-dd HH:mm",
                Locale.getDefault()
            )

        formatter.parse(
            "$date $time"
        )?.time

    } catch (_: Exception) {
        null
    }
}