package com.aronid.weighttrackertft.ui.components.formScreen

import androidx.compose.runtime.Composable
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FormScreen(
    modifier: Modifier,
    innerPadding: PaddingValues,
    isContentScrolleable: Boolean = false,
    formContent: @Composable () -> Unit,
    formButton: @Composable () -> Unit
) {
    ConstraintLayout(modifier = modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
        val (formInputRef, formButtonRef) = createRefs()
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .constrainAs(formInputRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().then(if (isContentScrolleable) Modifier.verticalScroll(
                    rememberScrollState()) else Modifier),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                formContent()
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.2f)
                .wrapContentHeight()
                .constrainAs(formButtonRef) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                formButton()
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

    }
}