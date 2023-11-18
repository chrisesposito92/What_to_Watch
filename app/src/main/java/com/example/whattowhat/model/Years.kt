package com.example.whattowhat.model

object Years {
    val year_initial = "All Years"
    val yearList = (2023 downTo 1900).map { it.toString() }.toList()
    val years = listOf(year_initial) + yearList
}