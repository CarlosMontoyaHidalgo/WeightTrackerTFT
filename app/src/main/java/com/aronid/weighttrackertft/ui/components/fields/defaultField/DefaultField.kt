package com.aronid.weighttrackertft.ui.components.fields.defaultField

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R

@Composable
fun DefaultField(
    modifier: Modifier? = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions,
    iconId: Int? = null,
    validate: (String) -> String? = { null },
    showError: Boolean = false,
) {

    val errorMessage = if (showError) validate(text) else null

    modifier?.let {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = it) {
            OutlinedTextField(
                value = text,
                onValueChange = { onTextChange(it) },
                label = {
                    Text(
                        text = label,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                isError = errorMessage != null,
                trailingIcon = {
                    if (errorMessage != null) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_info),
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                leadingIcon = {
                    iconId?.let {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = label,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                keyboardOptions = keyboardOptions,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }


            )
        }
    }
}
