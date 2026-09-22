package com.campuspass.app

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.model.LoginRequest
import com.campuspass.app.data.model.RegisterRequest
import com.campuspass.app.data.model.SsoRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginState =
        MutableStateFlow<LoginState>(LoginState.Idle)

    val loginState: StateFlow<LoginState> =
        _loginState


    private val _registerState =
        MutableStateFlow<RegisterState>(RegisterState.Idle)

    val registerState: StateFlow<RegisterState> =
        _registerState


    // =====================================================
    // EMAIL / PASSWORD LOGIN
    // =====================================================

    fun login(
        context: Context,
        email: String,
        password: String
    ) {

        viewModelScope.launch {

            _loginState.value =
                LoginState.Loading

            try {

                val response =
                    RetrofitInstance.api.login(
                        LoginRequest(
                            email = email,
                            password = password
                        )
                    )

                /*
                 * The CampusPass API returns a JWT token
                 * when authentication succeeds.
                 *
                 * It does not currently return:
                 *
                 * "success": true
                 *
                 * Therefore the presence of a token is
                 * used to determine successful login.
                 */

                if (!response.token.isNullOrBlank()) {

                    val prefs =
                        UserPreferences(context)

                    prefs.saveToken(
                        response.token
                    )

                    prefs.saveUser(
                        response.user?.name ?: "",
                        response.user?.email ?: email
                    )

                    _loginState.value =
                        LoginState.Success

                } else {

                    _loginState.value =
                        LoginState.Error(
                            response.message
                                ?: "Login failed"
                        )
                }

            } catch (e: Exception) {

                _loginState.value =
                    LoginState.Error(
                        e.message
                            ?: "Network error"
                    )
            }
        }
    }


    // =====================================================
    // GOOGLE SSO
    // =====================================================

    fun googleSso(
        context: Context,
        idToken: String,
        mode: String
    ) {

        viewModelScope.launch {

            _loginState.value =
                LoginState.Loading

            try {

                val response =
                    RetrofitInstance.api.ssoLogin(
                        SsoRequest(
                            idToken = idToken,
                            mode = mode
                        )
                    )

                /*
                 * Use the same authentication rule as
                 * normal login: a returned JWT token
                 * means authentication succeeded.
                 */

                if (!response.token.isNullOrBlank()) {

                    val prefs =
                        UserPreferences(context)

                    prefs.saveToken(
                        response.token
                    )

                    prefs.saveUser(
                        response.user?.name ?: "",
                        response.user?.email ?: ""
                    )

                    _loginState.value =
                        LoginState.Success

                } else {

                    _loginState.value =
                        LoginState.Error(
                            response.message
                                ?: "Google sign-in failed"
                        )
                }

            } catch (e: Exception) {

                _loginState.value =
                    LoginState.Error(
                        e.message
                            ?: "Network error"
                    )
            }
        }
    }


    // =====================================================
    // REGISTER
    // =====================================================

    fun register(
        context: Context,
        name: String,
        email: String,
        studentNumber: String,
        password: String
    ) {

        viewModelScope.launch {

            _registerState.value =
                RegisterState.Loading

            try {

                val response =
                    RetrofitInstance.api.register(
                        RegisterRequest(
                            name = name,
                            email = email,
                            studentNumber = studentNumber,
                            password = password
                        )
                    )

                /*
                 * Registration also returns a JWT token
                 * when successful.
                 */

                if (!response.token.isNullOrBlank()) {

                    val prefs =
                        UserPreferences(context)

                    prefs.saveToken(
                        response.token
                    )

                    prefs.saveUser(
                        response.user?.name ?: name,
                        response.user?.email ?: email
                    )

                    _registerState.value =
                        RegisterState.Success

                } else {

                    _registerState.value =
                        RegisterState.Error(
                            response.message
                                ?: "Registration failed"
                        )
                }

            } catch (e: Exception) {

                _registerState.value =
                    RegisterState.Error(
                        e.message
                            ?: "Network error"
                    )
            }
        }
    }


    // =====================================================
    // RESET LOGIN STATE
    // =====================================================

    fun resetLoginState() {

        _loginState.value =
            LoginState.Idle
    }


    // =====================================================
    // RESET REGISTER STATE
    // =====================================================

    fun resetRegisterState() {

        _registerState.value =
            RegisterState.Idle
    }
}


// =========================================================
// LOGIN STATE
// =========================================================

sealed class LoginState {

    object Idle : LoginState()

    object Loading : LoginState()

    object Success : LoginState()

    data class Error(
        val message: String
    ) : LoginState()
}


// =========================================================
// REGISTER STATE
// =========================================================

sealed class RegisterState {

    object Idle : RegisterState()

    object Loading : RegisterState()

    object Success : RegisterState()

    data class Error(
        val message: String
    ) : RegisterState()
}