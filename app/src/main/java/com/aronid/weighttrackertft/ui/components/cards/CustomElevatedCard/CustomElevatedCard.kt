package com.aronid.weighttrackertft.ui.components.cards.CustomElevatedCard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

@Composable
fun CustomElevatedCard(
    iconResource: ImageVector = Icons.Filled.LocalFireDepartment,
    title: String = "Title",
    result: Int? = null,
    unitLabel: String = "unit",
    contentColor: Color = Color.Red,
    modifier: Modifier = Modifier,
    elevation: Dp = 6.dp,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    titleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    shape: Shape = RoundedCornerShape(16.dp),
    iconContentDescription: String? = null
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = shape
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val (icon, titleText, resultNumber, resultUnit) = createRefs()
            val guideline = createGuidelineFromStart(0.3f)

            Icon(
                imageVector = iconResource,
                contentDescription = iconContentDescription ?: "$title icon",
                tint = contentColor,
                modifier = Modifier
                    .size(48.dp)
                    .constrainAs(icon) {
                        centerVerticallyTo(parent)
                        start.linkTo(parent.start)
                        end.linkTo(guideline)
                    }
            )

            Text(
                text = title.uppercase(),
                style = MaterialTheme.typography.titleMedium,
                color = titleColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(titleText) {
                    top.linkTo(parent.top)
                    start.linkTo(guideline)
                    end.linkTo(parent.end)
                    bottom.linkTo(resultNumber.top, margin = 8.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Text(
                text = "$result",
                style = MaterialTheme.typography.displayMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(resultNumber) {
                    top.linkTo(titleText.bottom, 8.dp)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(guideline)
                    end.linkTo(resultUnit.start)
                    width = Dimension.preferredWrapContent
                }
            )

            Text(
                text = unitLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(resultUnit) {
                    start.linkTo(resultNumber.end)
                    end.linkTo(parent.end)
                    baseline.linkTo(resultNumber.baseline)
                }
            )
        }
    }
}

//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
//@Composable
//fun CustomElevatedCardPreview() {
//    MaterialTheme {
//        Column(
//            modifier = Modifier.padding(16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            CustomElevatedCard(
//                title = "Active Calories",
//                result = 1250,
//                unitLabel = "kcal",
//                contentColor = Color(0xFFFF5722)
//
//            )
//            CustomElevatedCard(
//                title = "Active Calories",
//                result = 1250,
//                unitLabel = "kcal",
//                contentColor = Color(0xFFFF5722)
//
//            )
//
//            CustomElevatedCard(
//                iconResource = Icons.Filled.FitnessCenter,
//                title = "Volumen",
//                result = 132323,
//                unitLabel = "kg",
//                contentColor = Color.Black
//            )
//        }
//    }
//}