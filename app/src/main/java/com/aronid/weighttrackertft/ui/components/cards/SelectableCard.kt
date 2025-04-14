package com.aronid.weighttrackertft.ui.components.cards

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@SuppressLint("AutoboxingStateCreation")
@Composable
fun SelectableCard(
    options: List<CardTextOptionData>,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        options.forEach { options ->
            MyCard(
                text = options.text,
                textAlignment = TextAlign.Center,
                textColor = Color.Black,
                selectedTextColor = Color.White,
                description = options.description,
                descriptionAlignment = TextAlign.Start,
                isSelected = selectedOptionId == options.id,
                selectedContainerColor = Color.Blue,
                padding = 16,
                width = 375,
                height = 125,
                containerColor = Color.Transparent,
                borderColor = if (selectedOptionId == options.id) Color.DarkGray else Color.Black,
                borderWidth = 5,
                onClick = { onOptionSelected(options.id) }
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}