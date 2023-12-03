package com.example.whattowhat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.whattowhat.network.OpenAIService

class GptViewModelFactory(private val openAiService: OpenAIService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GptViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GptViewModel(openAiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
