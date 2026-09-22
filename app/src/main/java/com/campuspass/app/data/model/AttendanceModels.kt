package com.campuspass.app.data.model

data class AttendanceScanRequest(
    val ticketId: Int
)

data class Attendance(
    val attendanceId: Int,
    val ticketId: Int,
    val eventId: Int,
    val userId: Int,
    val scannedByUserId: Int,
    val scannedAt: String? = null
)

data class AttendanceScanResponse(
    val attendance: Attendance
)