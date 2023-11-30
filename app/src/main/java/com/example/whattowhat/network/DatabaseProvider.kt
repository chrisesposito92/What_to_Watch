package com.example.whattowhat.network

import android.content.Context
import android.util.Log
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        Log.d("DatabaseProvider", "Here")
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "database-name2"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
