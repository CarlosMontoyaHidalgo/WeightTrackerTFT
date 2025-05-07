package com.aronid.weighttrackertft.ui.components.dropdown.languageDropdown

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aronid.weighttrackertft.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageDropdown(
    modifier: Modifier = Modifier
) {
    val viewModel: LanguageViewModel = hiltViewModel()
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf("English") }

    // Load the saved language when the composable is first composed
    LaunchedEffect(Unit) {
        val savedLanguage = viewModel.getLanguage()
        currentLanguage = savedLanguage
    }

    val languages = listOf(
        Pair(stringResource(id = R.string.english), R.drawable.flag_uk),
        Pair(stringResource(id = R.string.spanish), R.drawable.flag_spain)
    )
    var expanded by remember { mutableStateOf(false) }
    var buttonWidth by remember { mutableStateOf(0.dp) }

    val currentFlag = languages.find { it.first == currentLanguage }?.second ?: R.drawable.flag_uk

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { expanded = true }
            .padding(12.dp)
            .onSizeChanged { size ->
                buttonWidth = with(Density(1f)) { size.width.toDp() }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(8.dp))
        Text(
            text = currentLanguage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_down),
            contentDescription = stringResource(id = R.string.expand_language_menu),
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        modifier = Modifier
            .width(buttonWidth + 32.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp)
            )
            .shadow(4.dp, RoundedCornerShape(8.dp))
    ) {
        languages.forEach { (language, flag) ->
            DropdownMenuItem(
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(flag),
                            contentDescription = "Flag",
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = language,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (language == currentLanguage) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                },
                onClick = {
                    if (language != currentLanguage) {
                        currentLanguage = language
                        viewModel.saveLanguage(language)
                        // Update the app's locale and restart the activity
                        updateLocale(context, language)
                        (context as? android.app.Activity)?.recreate()
                    }
                    expanded = false
                },
                modifier = Modifier.padding(horizontal = 8.dp),
                trailingIcon = {
                    if (language == currentLanguage) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = stringResource(R.string.selected),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            )
        }
    }
}

private fun updateLocale(context: android.content.Context, language: String) {
    val locale = when (language) {
        "English" -> Locale("en")
        "Spanish" -> Locale("es")
        else -> Locale.getDefault()
    }
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}