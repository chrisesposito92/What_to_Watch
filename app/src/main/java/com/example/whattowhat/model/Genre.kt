package com.example.whattowhat.model

data class Genre(val id: Int, val name: String, val shortName: String = name) {
    override fun toString(): String {
        return name
    }
}