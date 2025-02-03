package com.aronid.weighttrackertft.ui.components.authForm

import android.util.Patterns
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton

//TODO: Verify email a traves de un correo electronico

@Composable
fun AuthForm(
    buttonText: String,
    onClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    val isFormValid = !emailError && !passwordError && email.isNotEmpty() && password.isNotEmpty()

    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it
            emailError = !Patterns.EMAIL_ADDRESS.matcher(it).matches()
        },
        label = { Text(stringResource(id = R.string.email)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email Icon"
            )
        },
        isError = emailError,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true
    )
    if (emailError) {
        Text(text = stringResource(id = R.string.invalid_email), color = Color.Red)
    }

    Spacer(modifier = Modifier.padding(8.dp))

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            passwordError = it.length < 6
        },
        label = { Text(stringResource(R.string.password)) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Password Icon"
            )
        },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(
                    imageVector = image,
                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                )
            }
        },
        isError = passwordError,
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
    if (passwordError) {
        Text(text = stringResource(id = R.string.password_error), color = Color.Red)
    }
    Spacer(modifier = Modifier.padding(vertical = 20.dp))
    CustomButton(
        text = buttonText,
        containerColor = Color.Black,
        textColor = Color.White,
        onClick = {
            onClick(email, password)
        },
        enabled = isFormValid
    )
}