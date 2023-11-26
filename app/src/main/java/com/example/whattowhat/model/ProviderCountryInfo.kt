package com.example.whattowhat.model

data class ProviderCountryInfo(
    val link: String?,
    val buy: List<Provider>?,
    val rent: List<Provider>?,
    val flatrate: List<Provider>?
    // Add other fields like flatrate if they are needed
)