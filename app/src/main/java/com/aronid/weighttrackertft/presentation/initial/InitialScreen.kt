package com.aronid.weighttrackertft.presentation.initial

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton
import com.aronid.weighttrackertft.ui.components.languageDropdown.LanguageDropdown
import com.aronid.weighttrackertft.ui.theme.Black
import com.aronid.weighttrackertft.ui.theme.Blue
import com.aronid.weighttrackertft.ui.theme.White
import java.util.Locale

@Preview(showBackground = true)
@Composable
fun InitialScreenPreview() {
    InitialScreen(innerPadding = PaddingValues())
}

@Composable
fun InitialScreen(
    innerPadding: PaddingValues,
    navigateToLogin: () -> Unit = {},
    navigateToSignUp: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(Locale.getDefault().displayLanguage) }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        White, Blue
                    ), 0f, 1500f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "App Logo",
            modifier = Modifier.clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.app_name),
            color = White,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.weight(1f))
        CustomButton(
            stringResource(id = R.string.sign_up_free),
            containerColor = White,
            textColor = Black,
            borderColor = Black,
            borderWidth = 2,
            onClick = navigateToSignUp
        )
        CustomButton(
            stringResource(id = R.string.continue_with_google),
            containerColor = White,
            textColor = Black,
            imageId = R.drawable.ic_google,
            onClick = { /* continueWithGoogle() */ }
        )
        CustomButton(
            stringResource(id = R.string.login),
            containerColor = Color.Transparent,
            textColor = Black,
            onClick = navigateToLogin
        )
        Spacer(modifier = Modifier.weight(1f))
        LanguageDropdown(
            currentLanguage = currentLanguage,
            onLanguageSelected = { selectedLanguage ->
                val languageCode = when (selectedLanguage) {
                    "English" -> "en"
                    "Spanish" -> "es"
                    else -> "en"
                }
                setAppLocale(context, languageCode)
                currentLanguage = selectedLanguage
            }
        )
    }
}

fun setAppLocale(context: Context, languageCode: String){
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources = context.resources
    val configuration = resources.configuration
    configuration.setLocale(locale)
    resources.updateConfiguration(configuration, resources.displayMetrics)
}