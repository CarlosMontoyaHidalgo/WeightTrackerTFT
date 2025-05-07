package com.aronid.weighttrackertft.ui.screens.auth.initial

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aronid.weighttrackertft.R
import com.aronid.weighttrackertft.ui.components.button.MyElevatedButton

@Composable
fun InitialScreen(
    innerPadding: PaddingValues,
    navigateToLogin: () -> Unit = {},
    navigateToSignUp: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    ),
                    startY = 0f,
                    endY = 800f
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val logoSize = minOf(screenWidth * 0.4f, 150.dp)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(logoSize + 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(logoSize + 16.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                                    Color.Transparent
                                ),
                                radius = logoSize.value / 2 + 8f
                            ),
                            shape = CircleShape
                        )
                )
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = stringResource(R.string.app_logo_description),
                    contentScale = ContentScale.Crop, // Fill the circle
                    modifier = Modifier
                        .size(logoSize)
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        )
                        .shadow(elevation = 8.dp, shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 32.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MyElevatedButton(
                text = stringResource(R.string.sign_up_free),
                onClick = navigateToSignUp,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp
            )

            Spacer(Modifier.height(16.dp))

            MyElevatedButton(
                text = stringResource(R.string.login),
                onClick = navigateToLogin,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp
            )
        }
    }
}