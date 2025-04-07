package com.aronid.weighttrackertft.ui.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import com.aronid.weighttrackertft.R

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BackButtonPreview(){
    val navHostController = NavHostController(
        context = LocalContext.current
    )
    BackButton(navHostController)
}

@Composable
fun BackButton(navHostController: NavHostController){
    ConstraintLayout (Modifier.fillMaxSize()){
        val (buttonRef) = createRefs()
        Box(Modifier.constrainAs(buttonRef) {
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }) {
            Button(
                onClick = { navHostController.popBackStack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .shadow(4.dp, CircleShape),
                border = BorderStroke(width = 2.dp, color = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_go_back),
                    contentDescription = "Volver atrás",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}