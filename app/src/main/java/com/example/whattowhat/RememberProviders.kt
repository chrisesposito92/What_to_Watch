package com.example.whattowhat

import android.content.Context

class RememberProviders {
    fun saveSelectedProviders(context: Context, selectedProviders: Set<String>) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with (sharedPreferences.edit()) {
            putStringSet("SelectedProviders", selectedProviders)
            apply()
        }
    }

    fun getSelectedProviders(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("SelectedProviders", setOf()) ?: setOf()
    }

    fun addToWatchlist(context: Context, movieId: Int) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val watchlist = sharedPreferences.getStringSet("Watchlist", mutableSetOf()) ?: mutableSetOf()
        watchlist.add(movieId.toString())
        with (sharedPreferences.edit()) {
            putStringSet("Watchlist", watchlist)
            apply()
        }
    }

    fun getWatchlist(context: Context): Set<String> {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("Watchlist", setOf()) ?: setOf()
    }


}