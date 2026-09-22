package com.campuspass.app.data

import android.content.Context
import com.campuspass.app.api.RetrofitInstance
import com.campuspass.app.data.local.AppDatabase
import com.campuspass.app.data.local.EventEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SyncManager {

    suspend fun syncEvents(context: Context): Boolean = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            val dao = db.eventDao()

            val response = RetrofitInstance.api.getEvents()
            if (response.success && response.events != null) {
                dao.clearAll()
                dao.insertAll(response.events.map { e ->
                    EventEntity(
                        id = e.id,
                        title = e.title,
                        description = e.description,
                        date = e.date,
                        time = e.time,
                        location = e.location,
                        capacity = e.capacity,
                        category = e.category,
                        syncStatus = "synced"
                    )
                })
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCachedEvents(context: Context): List<EventEntity> = withContext(Dispatchers.IO) {
        try {
            val db = AppDatabase.getDatabase(context)
            db.eventDao().getAll()
        } catch (e: Exception) {
            emptyList()
        }
    }
}