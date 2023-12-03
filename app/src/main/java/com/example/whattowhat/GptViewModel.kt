package com.example.whattowhat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.whattowhat.model.GptPrompt
import com.example.whattowhat.model.GptResponse
import com.example.whattowhat.network.OpenAIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class GptViewModel(private val openAiService: OpenAIService) : ViewModel() {
    val question = MutableStateFlow<String?>(null)
    val recommendations = MutableStateFlow<List<String>>(emptyList())

    init {
        fetchQuestion()
    }

    private fun fetchQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<GptResponse> = openAiService.getGptResponse(
                    GptPrompt("Create a multiple choice question about what type of movie the user wants to watch:", 50)
                ).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    viewModelScope.launch(Dispatchers.Main) {
                        question.value = responseBody?.choices?.firstOrNull()?.text
                    }
                } else {
                    // Handle API call failure
                    Log.e("GptViewModel", "API call failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GptViewModel", "API call exception", e)
            }
        }
    }

    fun submitAnswer(answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: Response<GptResponse> = openAiService.getGptResponse(
                    GptPrompt("Based on the answer [$answer], recommend 5 movies:", 100)
                ).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    viewModelScope.launch(Dispatchers.Main) {
                        recommendations.value = responseBody?.choices?.firstOrNull()?.text
                            ?.split("\n")?.filterNot { it.isBlank() } ?: emptyList()
                        question.value = null  // Clear the question after getting recommendations
                    }
                } else {
                    // Handle API call failure
                    Log.e("GptViewModel", "API call failed: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("GptViewModel", "API call exception", e)
            }
        }
    }
}