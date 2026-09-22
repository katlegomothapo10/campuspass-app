package com.campuspass.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.campuspass.app.UserPreferences
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.model.AttendanceScanRequest
import com.campuspass.app.ui.theme.PrimaryBlue
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.launch
import retrofit2.HttpException

private data class CampusPassQrPayload(
    val ticketId: Int,
    val eventId: Int,
    val qrCode: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    val gson = remember { Gson() }

    var isValidating by remember {
        mutableStateOf(false)
    }

    var scanSuccessful by remember {
        mutableStateOf(false)
    }

    var resultMessage by remember {
        mutableStateOf<String?>(null)
    }

    var scannedTicketId by remember {
        mutableStateOf<Int?>(null)
    }

    // =====================================================
    // SEND SCANNED TICKET TO BACKEND
    // =====================================================

    fun validateTicket(ticketId: Int) {

        coroutineScope.launch {

            isValidating = true
            scanSuccessful = false
            resultMessage = null
            scannedTicketId = ticketId

            try {

                val token = prefs.getToken()

                if (token.isNullOrBlank()) {
                    resultMessage =
                        "Your session has expired. Please log in again."

                    return@launch
                }

                RetrofitInstance.api.scanTicket(
                    token = "Bearer $token",
                    request = AttendanceScanRequest(
                        ticketId = ticketId
                    )
                )

                scanSuccessful = true

                resultMessage =
                    "Ticket validated successfully. Attendance has been recorded."

            } catch (e: HttpException) {

                scanSuccessful = false

                resultMessage =
                    when (e.code()) {

                        400 ->
                            "This ticket is invalid, cancelled, or has already been used."

                        401 ->
                            "Your session has expired. Please log in again."

                        403 ->
                            "You are not allowed to scan this ticket."

                        404 ->
                            "Ticket not found."

                        else ->
                            "Ticket validation failed. Please try again."
                    }

            } catch (e: Exception) {

                scanSuccessful = false

                resultMessage =
                    e.message
                        ?: "Unable to validate ticket."
            } finally {

                isValidating = false
            }
        }
    }


    // =====================================================
    // ZXING SCANNER
    // =====================================================

    val scanLauncher =
        rememberLauncherForActivityResult(
            contract = ScanContract()
        ) { result ->

            val contents = result.contents

            if (contents.isNullOrBlank()) {
                return@rememberLauncherForActivityResult
            }

            try {

                /*
                 * CampusPass QR payload:
                 *
                 * {
                 *   "ticketId": 1,
                 *   "eventId": 1,
                 *   "qrCode": "..."
                 * }
                 */

                val qrPayload =
                    gson.fromJson(
                        contents,
                        CampusPassQrPayload::class.java
                    )

                if (qrPayload.ticketId <= 0) {

                    scanSuccessful = false
                    resultMessage =
                        "This is not a valid CampusPass ticket."

                } else {

                    validateTicket(
                        qrPayload.ticketId
                    )
                }

            } catch (_: Exception) {

                scanSuccessful = false

                resultMessage =
                    "This QR code is not a valid CampusPass ticket."
            }
        }


    // =====================================================
    // OPEN CAMERA
    // =====================================================

    fun openScanner() {

        resultMessage = null
        scanSuccessful = false

        val options =
            ScanOptions().apply {

                setDesiredBarcodeFormats(
                    ScanOptions.QR_CODE
                )

                setPrompt(
                    "Scan a CampusPass ticket"
                )

                setBeepEnabled(true)

                setOrientationLocked(false)

                setBarcodeImageEnabled(false)
            }

        scanLauncher.launch(options)
    }


    // =====================================================
    // CAMERA PERMISSION
    // =====================================================

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                openScanner()

            } else {

                scanSuccessful = false

                resultMessage =
                    "Camera permission is required to scan tickets."
            }
        }


    fun startScanner() {

        when {

            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

                openScanner()
            }

            else -> {

                cameraPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
        }
    }


    // =====================================================
    // SCREEN
    // =====================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Scan Ticket"
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
                }
            )
        },

        containerColor =
            MaterialTheme.colorScheme.background

    ) { padding ->

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


            // =================================================
            // ICON
            // =================================================

            Icon(
                imageVector =
                    Icons.Default.QrCodeScanner,

                contentDescription = null,

                tint = PrimaryBlue,

                modifier =
                    Modifier.size(100.dp)
            )


            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )


            Text(
                text = "CampusPass Entry Scanner",

                fontSize = 24.sp,

                fontWeight =
                    FontWeight.Bold,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )


            Text(
                text =
                    "Scan a student's CampusPass QR ticket to validate entry and record attendance.",

                fontSize = 14.sp,

                color =
                    MaterialTheme.colorScheme.onSurfaceVariant,

                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(32.dp)
            )


            // =================================================
            // VALIDATING
            // =================================================

            if (isValidating) {

                CircularProgressIndicator(
                    color = PrimaryBlue
                )

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Validating ticket...",

                    fontWeight =
                        FontWeight.Medium
                )

            } else {

                // =================================================
                // RESULT
                // =================================================

                if (resultMessage != null) {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),

                        colors =
                            CardDefaults.cardColors(

                                containerColor =
                                    if (scanSuccessful)

                                        Color(0xFF10B981)
                                            .copy(alpha = 0.12f)

                                    else

                                        MaterialTheme
                                            .colorScheme
                                            .errorContainer
                            )
                    ) {

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Icon(
                                imageVector =
                                    if (scanSuccessful)

                                        Icons.Default.CheckCircle

                                    else

                                        Icons.Default.ErrorOutline,

                                contentDescription = null,

                                tint =
                                    if (scanSuccessful)

                                        Color(0xFF10B981)

                                    else

                                        MaterialTheme
                                            .colorScheme
                                            .error,

                                modifier =
                                    Modifier.size(48.dp)
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )


                            Text(
                                text =
                                    if (scanSuccessful)
                                        "Entry Valid"
                                    else
                                        "Scan Failed",

                                fontSize = 18.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )


                            Text(
                                text =
                                    resultMessage!!,

                                fontSize = 13.sp,

                                textAlign =
                                    TextAlign.Center,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )


                            if (
                                scannedTicketId != null
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(8.dp)
                                )

                                Text(
                                    text =
                                        "Ticket #$scannedTicketId",

                                    fontSize = 12.sp,

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(24.dp)
                    )
                }


                // =================================================
                // SCAN BUTTON
                // =================================================

                Button(
                    onClick = {
                        startScanner()
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),

                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.QrCodeScanner,

                        contentDescription = null
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Text(
                        text =
                            if (resultMessage == null)
                                "Scan QR Ticket"
                            else
                                "Scan Another Ticket",

                        fontSize = 16.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }
    }
}