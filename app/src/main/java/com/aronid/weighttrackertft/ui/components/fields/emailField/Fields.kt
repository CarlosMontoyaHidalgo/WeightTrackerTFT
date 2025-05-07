package com.aronid.weighttrackertft.ui.components.fields.emailField

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.fields.defaultField.DefaultField


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenExample() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Campo de email
            DefaultField(
                text = email,
                onTextChange = { email = it },
                label = "Correo electrónico",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                iconId = R.drawable.ic_email,
                validate = { input ->
                    if (showErrors) {
                        when {
                            input.isEmpty() -> "El email es requerido"
                            !input.contains("@") -> "Formato de email inválido"
                            else -> null
                        }
                    } else null
                },
                showError = showErrors
            )

            // Espacio entre campos
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            DefaultField(
                text = password,
                onTextChange = { password = it },
                label = "Contraseña",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                iconId = R.drawable.ic_lock,
                validate = { input ->
                    if (showErrors) {
                        when {
                            input.isEmpty() -> "La contraseña es requerida"
                            input.length < 8 -> "Mínimo 8 caracteres"
                            !input.any { it.isDigit() } -> "Debe contener un número"
                            else -> null
                        }
                    } else null
                },
                showError = showErrors
            )

            // Botón de validación
            Button(
                onClick = { showErrors = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("Validar campos")
            }
        }
    }
}

// Previsualización con error
@Preview(showBackground = true)
@Composable
fun ErrorExamplePreview() {
    MaterialTheme {
        DefaultField(
            text = "correo.invalido",
            onTextChange = {},
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            iconId = R.drawable.ic_email,
            validate = {
                if (!it.contains("@")) "Email inválido" else null
            },
            showError = true
        )
    }
}

// Previsualización sin error
@Preview(showBackground = true)
@Composable
fun ValidExamplePreview() {
    MaterialTheme {
        DefaultField(
            text = "usuario@dominio.com",
            onTextChange = {},
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            iconId = R.drawable.ic_email,
            validate = {
                if (!it.contains("@")) "Email inválido" else null
            },
            showError = true
        )
    }
}
