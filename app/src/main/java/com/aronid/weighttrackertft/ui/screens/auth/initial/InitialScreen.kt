package com.aronid.weighttrackertft.ui.screens.auth.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.CustomButton

@Composable
fun InitialScreen(
    innerPadding: PaddingValues,
    navigateToLogin: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
) {
    /*
    val viewModel: InitialViewModel = hiltViewModel()
    val context = LocalContext.current
    var currentLanguage by remember { mutableStateOf(Locale.getDefault().displayLanguage) }
*/
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.onPrimaryContainer,
                        MaterialTheme.colorScheme.primary
                    ), 0f, 1500f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = stringResource(id = R.string.app_logo_description),
            modifier = Modifier.clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.weight(1f))
        CustomButton(
            stringResource(id = R.string.sign_up_free),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            textColor = MaterialTheme.colorScheme.onBackground,
            borderColor = MaterialTheme.colorScheme.onBackground,
            borderWidth = 2,
            onClick = navigateToSignUp
        )
        /*
        CustomButton(
            stringResource(id = R.string.continue_with_google),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            textColor = MaterialTheme.colorScheme.onBackground,
            imageId = R.drawable.ic_google,
            onClick = { /* continueWithGoogle() */ }
        )
        */
        CustomButton(
            stringResource(id = R.string.login),
            containerColor = MaterialTheme.colorScheme.onPrimary,
            textColor = MaterialTheme.colorScheme.onBackground,
            borderColor = MaterialTheme.colorScheme.onBackground,
            borderWidth = 2,
            onClick = navigateToLogin
        )
        Spacer(modifier = Modifier.weight(1f))
//        LanguageDropdown(
//            currentLanguage = currentLanguage,
//            onLanguageSelected = { selectedLanguage ->
//                viewModel.changeLanguage(context, selectedLanguage)
//                currentLanguage = selectedLanguage
//            }
//        )
    }
}
