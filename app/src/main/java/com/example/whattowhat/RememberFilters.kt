package com.example.whattowhat

import android.content.Context

class RememberFilters {
    fun saveSelectedSortOption(context: Context, selectedSortOption: String) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("SelectedSortOption", selectedSortOption)
            apply()
        }
    }

    fun getSelectedSortOption(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("SelectedSortOption", "popularity.desc") ?: "popularity.desc"
    }

    fun saveSelectedGenre(context: Context, selectedGenre: Int) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("SelectedGenre", selectedGenre)
            apply()
        }
    }

    fun getSelectedGenre(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("SelectedGenre", 0) ?: 0
    }

    fun saveSelectedYear(context: Context, selectedYear: String) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("SelectedYear", selectedYear)
            apply()
        }
    }

    fun getSelectedYear(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getString("SelectedYear", "All Years") ?: "All Years"
    }

    fun saveIncludeAnimation(context: Context, includeAnimation: Boolean) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean("ExcludeAnimation", includeAnimation)
            apply()
        }
    }

    fun getIncludeAnimation(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("ExcludeAnimation", true) ?: true
    }

    fun saveMinRating(context: Context, minRating: Int) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("MinRating", minRating)
            apply()
        }
    }

    fun getMinRating(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("MinRating", 1) ?: 1
    }

    fun saveSelectedProviderId(context: Context, selectedProvider: Int) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("SelectedProvider", selectedProvider)
            apply()
        }
    }

    fun getSelectedProvider(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("SelectedProvider", 0) ?: 0
    }
}