package com.campuspass.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.campuspass.app.ui.screens.ChangePasswordScreen
import com.campuspass.app.ui.screens.EditProfileScreen
import com.campuspass.app.ui.screens.EventDetailsScreen
import com.campuspass.app.ui.screens.HelpSupportScreen
import com.campuspass.app.ui.screens.LoginScreen
import com.campuspass.app.ui.screens.MainScreen
import com.campuspass.app.ui.screens.MyEventsScreen
import com.campuspass.app.ui.screens.PrivacyPolicyScreen
import com.campuspass.app.ui.screens.QrScannerScreen
import com.campuspass.app.ui.screens.RegisterScreen
import com.campuspass.app.ui.screens.SettingsScreen
import com.campuspass.app.ui.screens.TermsOfServiceScreen
import com.campuspass.app.ui.theme.CampusPassTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CampusPassTheme {

                val navController = rememberNavController()
                val context = LocalContext.current
                val prefs = remember { UserPreferences(context) }

                var isCheckingAuth by remember {
                    mutableStateOf(true)
                }

                var isLoggedIn by remember {
                    mutableStateOf(false)
                }

                // Check whether a saved CampusPass session exists.
                LaunchedEffect(Unit) {
                    isLoggedIn = prefs.getToken() != null
                    isCheckingAuth = false
                }

                if (isCheckingAuth) {

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                } else {

                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "main" else "login"
                    ) {

                        // =========================
                        // LOGIN
                        // =========================

                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // =========================
                        // REGISTER
                        // =========================

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("main") {
                                        popUpTo("login") {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // MAIN APP
                        // =========================

                        composable("main") {
                            MainScreen(
                                onOpenSettings = {
                                    navController.navigate("settings")
                                },
                                onEventClick = { eventId ->
                                    navController.navigate("event/$eventId")
                                },
                                onLogout = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        prefs.clear()

                                        navController.navigate("login") {
                                            popUpTo("main") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // =========================
                        // EVENT DETAILS
                        // =========================

                        composable("event/{eventId}") { backStackEntry ->

                            val eventId = backStackEntry.arguments
                                ?.getString("eventId")
                                ?.toIntOrNull()
                                ?: 0

                            EventDetailsScreen(
                                eventId = eventId,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // SETTINGS
                        // =========================

                        composable("settings") {
                            SettingsScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onLogout = {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        prefs.clear()

                                        navController.navigate("login") {
                                            popUpTo("main") {
                                                inclusive = true
                                            }
                                        }
                                    }
                                },
                                onOpenChangePassword = {
                                    navController.navigate("changePassword")
                                },
                                onOpenPrivacyPolicy = {
                                    navController.navigate("privacy")
                                },
                                onOpenTermsOfService = {
                                    navController.navigate("terms")
                                },
                                onOpenHelp = {
                                    navController.navigate("help")
                                }
                            )
                        }

                        // =========================
                        // EDIT PROFILE
                        // =========================

                        composable("editProfile") {
                            EditProfileScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onSave = { name, email ->
                                    CoroutineScope(Dispatchers.Main).launch {
                                        prefs.saveUser(name, email)
                                    }
                                }
                            )
                        }

                        // =========================
                        // ORGANIZER EVENTS
                        // =========================

                        composable("myEvents") {
                            MyEventsScreen(
                                onBack = {
                                    navController.popBackStack()
                                },
                                onScanTickets = {
                                    navController.navigate("qrScanner")
                                }
                            )
                        }

                        // =========================
                        // QR SCANNER
                        // =========================

                        composable("qrScanner") {
                            QrScannerScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // CHANGE PASSWORD
                        // =========================

                        composable("changePassword") {
                            ChangePasswordScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // PRIVACY
                        // =========================

                        composable("privacy") {
                            PrivacyPolicyScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // TERMS
                        // =========================

                        composable("terms") {
                            TermsOfServiceScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // =========================
                        // HELP
                        // =========================

                        composable("help") {
                            HelpSupportScreen(
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}