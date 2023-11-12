package com.example.whattowhat.model

data class Provider(
    val display_priorities: Map<String, Int>,
    val display_priority: Int,
    val logo_path: String,
    val provider_name: String,
    val provider_id: Int
)