package com.aronid.weighttrackertft.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aronid.weighttrackertft.ui.components.button.types.BackButton

@Composable
fun MyTopNavigationBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Black,
    shape: Shape = RoundedCornerShape(0.dp),
    /*actions: @Composable (() -> Unit)? = null*/
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor, shape)
            .padding(horizontal = 8.dp)
    ) {
        val (backButtonRef, titleTextRef) = createRefs()

        // Botón de retroceso solo si onBackClick no es nulo
        onBackClick?.let {
            BackButton(
                onClick = it,
                shape = RoundedCornerShape(8.dp),
                containerColor = Color.Transparent,
                contentColor = contentColor,
                iconSize = 24.dp,
                modifier = Modifier.constrainAs(backButtonRef) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
        }

        // Título centrado en la parte superior
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                ),
                modifier = Modifier.constrainAs(titleTextRef) {
                    start.linkTo(if (onBackClick != null) backButtonRef.end else parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                },
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }

        // Acciones a la derecha
//        actions?.let {
//            Row(
//                modifier = Modifier.constrainAs(actionsRef) {
//                    end.linkTo(parent.end)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                },
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                it()
//            }
//        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyTopNavigationBar() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MyTopNavigationBar(
            title = "My Routine",
            onBackClick = { /* Acción */ },
            backgroundColor = Color.White,
            contentColor = Color.Black,
        )

        MyTopNavigationBar(
            title = "No Back Button",
            onBackClick = null,
            backgroundColor = Color.LightGray,
            contentColor = Color.DarkGray,
        )
    }
}