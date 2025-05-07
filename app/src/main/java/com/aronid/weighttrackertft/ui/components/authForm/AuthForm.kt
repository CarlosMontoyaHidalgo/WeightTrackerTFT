package com.aronid.weighttrackertft.ui.components.authForm

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton
import com.aronid.weighttrackertft.ui.components.fields.emailField.EmailField
import com.aronid.weighttrackertft.ui.components.passwordField.PasswordField

@Composable
fun AuthForm(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isEnabled: Boolean,
    isLoading: Boolean,
    emailError: String?,
    passwordError: String?,
    formError: String?,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier
        .padding(16.dp)
        .fillMaxHeight()) {
        EmailField(
            email = email,
            onEmailChange = onEmailChange,
            errorMessage = emailError
        )
        Spacer(modifier = Modifier.padding(8.dp))

        PasswordField(
            password = password,
            onPasswordChange = onPasswordChange,
            errorMessage = passwordError,
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it }
        )
        Spacer(modifier = Modifier.padding(vertical = 20.dp))

        formError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            MyElevatedButton(
                text = buttonText,
                onClick = onSubmit,
                enabled = isEnabled,
                buttonColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary,
                height = 54.dp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



