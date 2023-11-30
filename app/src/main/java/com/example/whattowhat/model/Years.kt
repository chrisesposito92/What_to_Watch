package com.example.whattowhat.model

object Years {
    private const val year_initial = "All Years"
    private val yearList = (2023 downTo 1900).map { it.toString() }.toList()
    val years = listOf(year_initial) + yearList
}