package com.aronid.weighttrackertft.ui.components.searchBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.exerciseCard.ExerciseCard
import com.aronid.weighttrackertft.ui.components.tags.MyTag

@Preview(showSystemUi = true)
@Composable
fun MySearchBar() {
    val viewModel = viewModel<SearchBarViewModel>()
    val searchText by viewModel.searchText.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val selectedMuscle by viewModel.selectedMuscle.collectAsState()



    Column(
        modifier = Modifier.fillMaxWidth()

    ) {
        TextField(
            value = searchText,
            onValueChange = viewModel::onSearchTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Search")
                }
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_x),
                        contentDescription = null,
                        modifier = Modifier

                            .clickable { viewModel.clearAllFilters() }
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.muscleList.distinct()) { muscle ->
                val isSelected = muscle == selectedMuscle
                MyTag(
                    text = muscle,
                    textColor = if (isSelected) Color.White else Color.Black,
                    backgroundColor = if (isSelected) Color.Blue else Color.LightGray,
                    onClick = { viewModel.toggleMuscleFilter(muscle) }
                )
            }
        }

        selectedMuscle?.let { muscle ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { viewModel.toggleMuscleFilter(muscle) }
                    .padding(8.dp)
            ) {
                Text("Filtrado por: $muscle", color = Color.Blue)
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_x),
                    contentDescription = "Quitar filtro",
                    tint = Color.Blue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isSearching) {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(exercises) { exercise ->
                    ExerciseCard(
                        exerciseName = exercise.name,
                        muscles = exercise.muscles,
                        imageResId = exercise.image
                    )
                }
            }
        }

    }
}

//@Composable
//fun FilterByMuscle(muscle: String) {
//    LazyRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(32.dp)
//    ) {
//        items(listOf(muscle)) { muscle ->
//            MyTag(
//                text = muscle,
//                textColor = Color.White,
//                backgroundColor = Color.Black
//            )
//        }
//    }
//}

