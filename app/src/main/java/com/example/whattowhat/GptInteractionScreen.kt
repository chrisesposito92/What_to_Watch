package com.example.whattowhat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun GptInteractionScreen(
    question: State<String?>,
    recommendations: State<List<String>>,
    viewModel: GptViewModel
) {
    when {
        question.value != null -> QuestionScreen(question.value!!) { answer ->
            viewModel.submitAnswer(answer)
        }
        recommendations.value.isNotEmpty() -> RecommendationsScreen(recommendations.value)
        else -> CircularProgressIndicator()
    }
}


@Composable
fun QuestionScreen(question: String, onAnswer: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
 //       Text(question, style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(16.dp))
        // Assuming options are part of the question text and separated by line breaks
        val options = question.split("\n").drop(1)
        options.forEach { option ->
            Button(onClick = { onAnswer(option) }, modifier = Modifier.padding(8.dp)) {
                Text(option)
            }
        }
    }
}


@Composable
fun RecommendationsScreen(recommendations: List<String>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Movie Recommendations", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(recommendations.size) { index ->
                val movie = recommendations[index]
                Text(movie, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

