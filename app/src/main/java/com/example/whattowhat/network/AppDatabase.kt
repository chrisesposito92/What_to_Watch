package com.example.whattowhat.network

import com.example.whattowhat.network.WatchlistDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.whattowhat.model.WatchlistItem

@Database(entities = [WatchlistItem::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "whattowatch_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

    }
}
