package com.campuspass.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    StaticContentScreen(
        title = "Privacy Policy",
        content = """
            CampusPass Privacy Policy
            
            Last updated: September 2026
            
            1. Information We Collect
            We collect your name, email, and student number when you register.
            
            2. How We Use Your Information
            Your information is used to manage your account, register you for events, and send notifications.
            
            3. Data Storage
            Data is stored securely on cloud servers and encrypted in transit.
            
            4. Third-Party Services
            We use Google Sign-In for authentication and Firebase for notifications.
            
            5. Your Rights
            You can request deletion of your account at any time by contacting support.
            
            6. Contact
            For questions, email privacy@campuspass.co.za
        """.trimIndent(),
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServiceScreen(onBack: () -> Unit) {
    StaticContentScreen(
        title = "Terms of Service",
        content = """
            CampusPass Terms of Service
            
            Last updated: September 2026
            
            1. Acceptance
            By using CampusPass, you agree to these terms.
            
            2. User Responsibilities
            You are responsible for maintaining the confidentiality of your account.
            
            3. Acceptable Use
            You may not use CampusPass for illegal activities.
            
            4. Event Registration
            Registration is binding once confirmed. Cancellations must follow event-specific policies.
            
            5. Changes to Terms
            We may update these terms. Continued use implies acceptance.
            
            6. Limitation of Liability
            CampusPass is not responsible for event cancellations or changes.
        """.trimIndent(),
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            HelpItem("How do I register for an event?", "Tap any event on the Home screen, then tap Register.")
            HelpItem("How do I get my ticket?", "After registering, go to the Tickets tab to see your QR code.")
            HelpItem("How do I scan tickets?", "Organizers can use the Scan option (coming soon).")
            HelpItem("How do I change my language?", "Go to Settings and select your preferred language.")
            HelpItem("How do I log out?", "Tap your profile icon and select Logout.")
            HelpItem("Contact us", "Email: support@campuspass.co.za")

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF3B5BDB))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Contact Support", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text("support@campuspass.co.za", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun HelpItem(question: String, answer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(question, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(answer, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(onBack: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Password", fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            if (error.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(error, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            if (success) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Password changed successfully!", color = Color(0xFF10B981), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    error = ""
                    success = false
                    when {
                        currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank() ->
                            error = "Please fill in all fields"
                        newPassword != confirmPassword ->
                            error = "New passwords do not match"
                        newPassword.length < 6 ->
                            error = "Password must be at least 6 characters"
                        else -> {
                            success = true
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Change Password", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StaticContentScreen(
    title: String,
    content: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Text(
                    content,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}