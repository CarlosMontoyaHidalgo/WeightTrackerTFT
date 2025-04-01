package com.aronid.weighttrackertft.ui.screens.personalization

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.ui.components.dropdown.languageDropdown.LanguageDropdown
import com.aronid.weighttrackertft.ui.components.dropdown.weightReminder.WeightReminderSelector
import com.aronid.weighttrackertft.ui.components.dropdown.weightReminder.WeightReminderViewModel
import com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector.WeightUnitSelector
import com.aronid.weighttrackertft.ui.components.dropdown.weightUnitSelector.WeightUnitSelectorViewModel
import com.aronid.weighttrackertft.ui.screens.auth.initial.InitialViewModel
import java.util.Locale

@Composable
fun PersonalizationScreen(innerPadding: PaddingValues, navHostController: NavHostController){
    val languageViewModel: InitialViewModel = hiltViewModel()
    val weightReminderViewModel: WeightReminderViewModel = hiltViewModel()
    val weightUnitSelectorViewModel: WeightUnitSelectorViewModel = hiltViewModel()
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(Locale.getDefault().displayLanguage) }


    Column(Modifier.padding(innerPadding)){
//        LanguageDropdown(
//            currentLanguage = currentLanguage,
//            onLanguageSelected = { selectedLanguage ->
//                languageViewModel.changeLanguage(context, selectedLanguage)
//                currentLanguage = selectedLanguage
//            }
//        )

        LanguageDropdown()

        WeightReminderSelector(
            viewModel = weightReminderViewModel
        )

        WeightUnitSelector(
            viewModel = weightUnitSelectorViewModel
        )
    }
}