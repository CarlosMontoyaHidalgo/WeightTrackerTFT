package com.aronid.weighttrackertft.ui.components.searchBar

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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

                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(8.dp))


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


