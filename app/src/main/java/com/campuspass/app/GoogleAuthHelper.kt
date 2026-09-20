package com.campuspass.app

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

object GoogleAuthHelper {

    private const val TAG = "GoogleAuthHelper"
    private const val WEB_CLIENT_ID = "112024644835-rcf5shq38j9miqgal408kas50s1ca01g.apps.googleusercontent.com"

    suspend fun signIn(context: Context): String? {
        return try {
            Log.d(TAG, "Starting Google Sign-In")
            Log.d(TAG, "Client ID: $WEB_CLIENT_ID")

            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            Log.d(TAG, "Requesting credential...")

            val result: GetCredentialResponse = credentialManager.getCredential(
                context = context,
                request = request
            )

            Log.d(TAG, "Got credential type: ${result.credential.type}")

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                Log.d(TAG, "SUCCESS: Got ID token")
                googleIdTokenCredential.idToken
            } else {
                Log.e(TAG, "ERROR: Unexpected credential type: ${credential.type}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "FAILED: ${e.javaClass.simpleName} - ${e.message}", e)
            null
        }
    }
}