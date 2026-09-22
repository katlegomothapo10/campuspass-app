package com.campuspass.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.campuspass.app.UserPreferences
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.SyncManager
import com.campuspass.app.data.model.Event
import com.campuspass.app.ui.theme.PrimaryBlue
import com.campuspass.app.ui.theme.PrimaryBlueDark
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenSettings: () -> Unit,
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit,
    onEventClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("Student") }
    var allEvents by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isOffline by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf(
        "All",
        "Academic",
        "Social",
        "Sports",
        "Cultural"
    )

    fun loadEvents() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitInstance.api.getEvents()

                allEvents = response.events
                isOffline = false

                // Store latest server events locally for offline use.
                try {
                    SyncManager.syncEvents(context)
                } catch (_: Exception) {
                    // The online data has already loaded successfully,
                    // so a cache failure should not block the screen.
                }

            } catch (e: Exception) {

                try {
                    val cachedEvents = SyncManager.getCachedEvents(context)

                    if (cachedEvents.isNotEmpty()) {
                        allEvents = cachedEvents.map { cached ->
                            Event(
                                eventId = cached.id,
                                title = cached.title,
                                description = cached.description,
                                date = cached.date,
                                time = cached.time,
                                location = cached.location,
                                capacity = cached.capacity,
                                category = cached.category ?: "other",
                                clubId = null,
                                organizerUserId = 0,
                                waitlistEnabled = 1,
                                registeredCount = 0,
                                status = null
                            )
                        }

                        isOffline = true
                        errorMessage = null

                    } else {
                        errorMessage =
                            "Cannot reach the CampusPass API and no cached events are available."
                    }

                } catch (_: Exception) {
                    errorMessage =
                        e.message ?: "Unable to load events."
                }

            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        userName = prefs.getUserName() ?: "Student"
        loadEvents()
    }

    val filteredEvents = allEvents.filter { event ->

        val matchesCategory =
            selectedCategory == "All" ||
                    event.category.equals(
                        selectedCategory,
                        ignoreCase = true
                    )

        val matchesSearch =
            searchQuery.isBlank() ||
                    event.title.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    event.description?.contains(
                        searchQuery,
                        ignoreCase = true
                    ) == true ||
                    event.location.contains(
                        searchQuery,
                        ignoreCase = true
                    ) ||
                    event.category.contains(
                        searchQuery,
                        ignoreCase = true
                    )

        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome back,",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = userName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onOpenProfile
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                bottom = 16.dp
            )
        ) {

            // =========================
            // OFFLINE INDICATOR
            // =========================

            if (isOffline) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Text(
                            text = "Offline mode — showing cached events",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(10.dp),
                            color =
                                MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // =========================
            // SEARCH
            // =========================

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    placeholder = {
                        Text(
                            text = "Search events, categories...",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // =========================
            // LOADING
            // =========================

            if (isLoading) {

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryBlue
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Loading events...",
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

            } else if (errorMessage != null) {

                // =========================
                // ERROR
                // =========================

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Couldn't load events",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = errorMessage ?: "Unknown error",
                            fontSize = 12.sp,
                            color =
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )

                        Button(
                            onClick = {
                                loadEvents()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Try Again")
                        }
                    }
                }

            } else {

                // =========================
                // UPCOMING EVENTS
                // =========================

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Upcoming Events",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(
                            onClick = {
                                selectedCategory = "All"
                                searchQuery = ""
                            }
                        ) {
                            Text(
                                text = "See all",
                                color = PrimaryBlue,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                if (filteredEvents.isNotEmpty()) {

                    item {
                        LazyRow(
                            contentPadding = PaddingValues(
                                horizontal = 16.dp
                            ),
                            horizontalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = filteredEvents,
                                key = { event ->
                                    event.eventId
                                }
                            ) { event ->

                                EventCard(
                                    event = event,
                                    onClick = {
                                        onEventClick(
                                            event.eventId
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // =========================
                // CATEGORIES
                // =========================

                item {
                    Text(
                        text = "Categories",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 20.dp,
                            bottom = 12.dp
                        )
                    )
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp
                        ),
                        horizontalArrangement =
                            Arrangement.spacedBy(10.dp)
                    ) {
                        items(categories) { category ->

                            CategoryChip(
                                category = category,
                                isSelected =
                                    selectedCategory == category,
                                onClick = {
                                    selectedCategory = category
                                }
                            )
                        }
                    }
                }

                // =========================
                // ALL EVENTS
                // =========================

                item {
                    Text(
                        text =
                            "All Events (${filteredEvents.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 24.dp,
                            bottom = 12.dp
                        )
                    )
                }

                items(
                    items = filteredEvents,
                    key = { event ->
                        event.eventId
                    }
                ) { event ->

                    EventListCard(
                        event = event,
                        onClick = {
                            onEventClick(
                                event.eventId
                            )
                        }
                    )
                }

                if (filteredEvents.isEmpty()) {

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.EventBusy,
                                contentDescription = null,
                                tint =
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    if (allEvents.isEmpty())
                                        "No events available yet"
                                    else
                                        "No events found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

                            Text(
                                text =
                                    if (allEvents.isEmpty())
                                        "New campus events will appear here."
                                    else
                                        "Try changing your search or category.",
                                fontSize = 12.sp,
                                color =
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


// =====================================================
// FEATURED EVENT CARD
// =====================================================

@Composable
fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    val eventColor =
        getEventColor(event.category)

    Card(
        modifier = Modifier
            .width(220.dp)
            .height(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                eventColor,
                                eventColor.copy(
                                    alpha = 0.7f
                                )
                            )
                        )
                    ),
                contentAlignment =
                    Alignment.BottomStart
            ) {
                Surface(
                    modifier = Modifier.padding(10.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 10.dp,
                            vertical = 6.dp
                        ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getEventDay(
                                event.date
                            ),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlueDark
                        )

                        Text(
                            text = getEventMonth(
                                event.date
                            ),
                            fontSize = 10.sp,
                            fontWeight =
                                FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.LocationOn,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )

                    Text(
                        text = event.location,
                        fontSize = 11.sp,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(start = 2.dp)
                            .weight(1f)
                    )
                }
            }
        }
    }
}


// =====================================================
// CATEGORY CHIP
// =====================================================

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color =
            if (isSelected)
                PrimaryBlue
            else
                MaterialTheme.colorScheme.surface,
        border =
            if (isSelected)
                null
            else
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                ),
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {
            Icon(
                imageVector =
                    when (category) {
                        "Academic" ->
                            Icons.Default.School

                        "Social" ->
                            Icons.Default.People

                        "Sports" ->
                            Icons.Default.SportsBasketball

                        "Cultural" ->
                            Icons.Default.TheaterComedy

                        "All" ->
                            Icons.Default.GridView

                        else ->
                            Icons.Default.MoreHoriz
                    },
                contentDescription = null,
                tint =
                    if (isSelected)
                        Color.White
                    else
                        PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = category,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color =
                    if (isSelected)
                        Color.White
                    else
                        MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


// =====================================================
// EVENT LIST CARD
// =====================================================

@Composable
fun EventListCard(
    event: Event,
    onClick: () -> Unit
) {
    val eventColor =
        getEventColor(event.category)

    val spotsLeft =
        (event.capacity - event.registeredCount)
            .coerceAtLeast(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 6.dp
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    )
                    .background(
                        eventColor.copy(
                            alpha = 0.2f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    tint = eventColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text =
                        "${formatEventDate(event.date)} · ${event.time}",
                    fontSize = 11.sp,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        "$spotsLeft/${event.capacity} spots left",
                    fontSize = 11.sp,
                    color =
                        if (spotsLeft < 10)
                            Color(0xFFEF4444)
                        else
                            Color(0xFF10B981),
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector =
                    Icons.Default.ChevronRight,
                contentDescription = null,
                tint =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


// =====================================================
// HELPERS
// =====================================================

private fun getEventColor(
    category: String
): Color {
    return when (category.lowercase()) {
        "academic" -> Color(0xFF3B5BDB)
        "social" -> Color(0xFF7C3AED)
        "sports" -> Color(0xFF10B981)
        "cultural" -> Color(0xFFF59E0B)
        else -> Color(0xFF06B6D4)
    }
}

private fun getEventDay(
    date: String
): String {
    return try {
        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val parsed = input.parse(date)

        if (parsed != null) {
            SimpleDateFormat(
                "dd",
                Locale.getDefault()
            ).format(parsed)
        } else {
            "--"
        }
    } catch (_: Exception) {
        "--"
    }
}

private fun getEventMonth(
    date: String
): String {
    return try {
        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val parsed = input.parse(date)

        if (parsed != null) {
            SimpleDateFormat(
                "MMM",
                Locale.getDefault()
            )
                .format(parsed)
                .uppercase()
        } else {
            "---"
        }
    } catch (_: Exception) {
        "---"
    }
}

private fun formatEventDate(
    date: String
): String {
    return try {
        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val parsed = input.parse(date)

        if (parsed != null) {
            SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            ).format(parsed)
        } else {
            date
        }
    } catch (_: Exception) {
        date
    }
}