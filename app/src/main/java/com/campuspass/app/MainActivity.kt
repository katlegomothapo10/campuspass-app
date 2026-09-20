package com.campuspass.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.campuspass.app.ui.screens.*
import com.campuspass.app.ui.theme.CampusPassTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusPassTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val prefs = remember { UserPreferences(context) }

                var isCheckingAuth by remember { mutableStateOf(true) }
                var isLoggedIn by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    isLoggedIn = prefs.getToken() != null
                    isCheckingAuth = false
                }

                if (isCheckingAuth) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "main" else "login"
                    ) {
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = {
                                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                                },
                                onNavigateToRegister = { navController.navigate("register") }
                            )
                        }

                        composable("register") {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                                },
                                onNavigateToLogin = { navController.popBackStack() }
                            )
                        }

                        composable("main") {
                            MainScreen(
                                onOpenSettings = { navController.navigate("settings") },
                                onOpenEventDetails = { title -> navController.navigate("event/$title") },
                                onOpenEditProfile = { navController.navigate("editProfile") },
                                onOpenMyEvents = { navController.navigate("myEvents") },
                                onLogout = {
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        prefs.clear()
                                        navController.navigate("login") { popUpTo("main") { inclusive = true } }
                                    }
                                }
                            )
                        }

                        composable("settings") {
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onLogout = {
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        prefs.clear()
                                        navController.navigate("login") { popUpTo("main") { inclusive = true } }
                                    }
                                },
                                onOpenChangePassword = { navController.navigate("changePassword") },
                                onOpenPrivacyPolicy = { navController.navigate("privacy") },
                                onOpenTermsOfService = { navController.navigate("terms") },
                                onOpenHelp = { navController.navigate("help") }
                            )
                        }

                        composable(
                            "event/{title}",
                            arguments = listOf(navArgument("title") { type = NavType.StringType })
                        ) { entry ->
                            EventDetailsScreen(
                                eventTitle = entry.arguments?.getString("title") ?: "",
                                onBack = { navController.popBackStack() },
                                onRegister = { }
                            )
                        }

                        composable("editProfile") {
                            EditProfileScreen(
                                onBack = { navController.popBackStack() },
                                onSave = { name, email ->
                                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                        prefs.saveUser(name, email)
                                    }
                                }
                            )
                        }

                        composable("myEvents") {
                            MyEventsScreen(onBack = { navController.popBackStack() })
                        }

                        composable("changePassword") {
                            ChangePasswordScreen(onBack = { navController.popBackStack() })
                        }

                        composable("privacy") {
                            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
                        }

                        composable("terms") {
                            TermsOfServiceScreen(onBack = { navController.popBackStack() })
                        }

                        composable("help") {
                            HelpSupportScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}