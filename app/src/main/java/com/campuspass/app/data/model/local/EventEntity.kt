package com.campuspass.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String?,
    val date: String,
    val time: String,
    val location: String,
    val capacity: Int,
    val category: String?,
    val syncStatus: String = "synced"
)