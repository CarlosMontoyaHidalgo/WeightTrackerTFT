package com.aronid.weighttrackertft.ui.components.languageDropdown

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun LanguageDropdown(currentLanguage: String, onLanguageSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    val languages = listOf(stringResource(id = R.string.english), stringResource(id = R.string.spanish))
    var expanded by remember { mutableStateOf(false) }

    Column (modifier = Modifier.fillMaxWidth().padding(16.dp)){
        Text(text = currentLanguage, modifier = Modifier.clickable { expanded = true }.padding(bottom = 8.dp))
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        languages.forEach { language ->
            DropdownMenuItem(
                text = { Text(text = language) },
                onClick = {
                onLanguageSelected(language)
                expanded = false
            })
        }
    }
}
