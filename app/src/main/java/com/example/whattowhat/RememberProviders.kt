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

}