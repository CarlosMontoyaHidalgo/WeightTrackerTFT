package com.aronid.weighttrackertft.ui.components.searchBar


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun SearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    TextField(
        value = searchText,
        onValueChange = onSearchTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        placeholder = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.search_routines_placeholder))
            }
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = onClearSearch) {
                    Icon(
                        painter = painterResource(R.drawable.ic_x),
                        contentDescription = stringResource(R.string.close)
                    )
                }
            }
        },
        singleLine = true
    )
}