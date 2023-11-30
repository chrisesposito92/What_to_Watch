package com.example.whattowhat

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("SuspiciousIndentation")
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    gridState: LazyGridState,
    onPageChange: (Int) -> Unit
) {
    val currentPageString = "$currentPage of $totalPages"
    val textFieldWidth = (currentPageString.length * 10 + 45).dp
    LaunchedEffect(currentPage) {
        gridState.scrollToItem(index = 0)
    }
    Button(
        onClick = { onPageChange(1) },
        enabled = currentPage > 2,
        // Apply MaterialTheme styling
    ) {
        Text("First")
    }
    Button(
        onClick = { onPageChange(currentPage - 1) },
        enabled = currentPage > 1,
        // Apply MaterialTheme styling
    ) {
        Text("Previous")
    }

    // Current Page TextField with outlined style
    Text(
        text = currentPageString,
        style = TextStyle(
            fontSize = 24.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(textFieldWidth)
            .padding(8.dp)
            .border(1.dp, Color.Black, shape = RoundedCornerShape(50))
        // More MaterialTheme styling
    )

    Button(
        onClick = {
            onPageChange(currentPage + 1)
        },
        enabled = currentPage < totalPages
    ) {
        Text("Next")
    }

    Button(
        onClick = { onPageChange(totalPages) },
        enabled = currentPage < totalPages - 1,
        // Apply MaterialTheme styling
    ) {
        Text("Last")
    }

}