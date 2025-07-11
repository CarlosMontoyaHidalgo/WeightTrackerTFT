package com.aronid.weighttrackertft.ui.components.exercise.exerciseCard

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.ui.components.tags.MyTag
import com.aronid.weighttrackertft.utils.Translations.exerciseTranslations
import com.aronid.weighttrackertft.utils.Translations.muscleTranslations
import com.aronid.weighttrackertft.utils.Translations.translateAndFormat

@Composable
fun ExerciseCard(
    name: String,
    primaryMuscle: String,
    secondaryMuscles: List<String>,
    @DrawableRes imageResId: Int,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    isSelected: Boolean = false,
    onToggleSelection: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .then(
                if (isSelected) Modifier.border(
                    BorderStroke(4.dp, MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(imageResId),
                contentDescription = translateAndFormat(name, exerciseTranslations),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(128.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = translateAndFormat(name, exerciseTranslations),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        MyTag(
                            text = translateAndFormat(primaryMuscle, muscleTranslations),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                    items(secondaryMuscles) { muscle ->
                        MyTag(
                            text = translateAndFormat(muscle, muscleTranslations),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}