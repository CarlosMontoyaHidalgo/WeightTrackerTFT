package com.aronid.weighttrackertft.ui.components.fields.email

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.fields.defaultField.DefaultField

@Composable
fun EmailField(viewModel: EmailFieldViewModel) {
    val email by viewModel.email.collectAsState()
    val showError by viewModel.showError.collectAsState()

    DefaultField(
        text = email,
        onTextChange = { viewModel.onEmailChange(it) },
        label = "Correo Electronico",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        iconId = R.drawable.ic_email,
        validate = viewModel::validateEmail,
        showError = showError
    )

}

@Preview
@Composable
fun EmailFieldScreenPreview() {
    val viewModel: EmailFieldViewModel = hiltViewModel()
    EmailField(viewModel)
}