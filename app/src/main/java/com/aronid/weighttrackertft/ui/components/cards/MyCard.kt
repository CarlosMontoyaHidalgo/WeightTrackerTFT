package com.aronid.weighttrackertft.ui.components.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aronid.weighttrackertft.R


@Preview
@Composable
fun MyCardPreview() {
    Column {
        MyCard(
            text = "My Card",
            textAlignment = TextAlign.Center,
            description = "This is a card Lorem ipsum asdmndnad,n ad sdnaskjd naskjndjkdnaks dasjkdnaks",
            descriptionAlignment = TextAlign.Start,
            textColor = Color.Black,
            selectedTextColor = Color.White,
            padding = 16,
            width = 240,
            height = 100,
            containerColor = Color.Gray,
            borderColor = Color.DarkGray,
            borderWidth = 2
        )
        MyCard(
            imageId = R.drawable.ic_man,
            textColor = Color.Black,
            selectedTextColor = Color.White,
            padding = 16,
            width = 240,
            height = 100,
            containerColor = Color.Gray,
            borderColor = Color.DarkGray,
            borderWidth = 2,
            onClick = {}
        )
        MyCard(
            imageId = R.drawable.ic_man,
            isSelected = true,
            textColor = Color.Black,
            selectedTextColor = Color.White,
            padding = 16,
            width = 100,
            height = 75,
            containerColor = Color.Gray,
            borderColor = Color.DarkGray,
            borderWidth = 2,
            onClick = {}
        )
    }
}

@Composable
fun MyCard(
    text: String? = null,
    textAlignment: TextAlign? = null,
    textColor: Color,
    selectedTextColor: Color? = null,
    description: String? = null,
    descriptionAlignment: TextAlign? = null,
    imageId: Int? = null,
    isSelected: Boolean = false,
    selectedContainerColor: Color? = null,
    padding: Int,
    width: Int,
    height: Int,
    containerColor: Color,
    borderColor: Color,
    borderWidth: Int,
    onClick: () -> Unit = {}
) {

    val actualContainerColor = selectedContainerColor ?: containerColor
    val actualTextColor = selectedTextColor ?: textColor

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) actualContainerColor else containerColor,
        ),
        border = BorderStroke(borderWidth.dp, borderColor),
        modifier = Modifier.size(width = width.dp, height = height.dp)
    ) {

        if (text != null || description != null) {
            Text(
                text = text ?: "",
                modifier = Modifier
                    .padding(horizontal = padding.dp, vertical = padding.dp)
                    .fillMaxWidth()
                    .clickable { onClick() },
                textAlign = textAlignment,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = if (isSelected) actualTextColor else textColor,
                maxLines = 1,
            )
            Text(
                text = description ?: "",
                modifier = Modifier
                    .padding(horizontal = padding.dp)
                    .fillMaxWidth()
                    .clickable { onClick() },
                textAlign = descriptionAlignment,
                fontWeight = FontWeight.Light,
                color = if (isSelected) actualTextColor else textColor,
                maxLines = 2,
                overflow = TextOverflow.Clip,
                softWrap = true
            )
        } else if (imageId != null) {
            Icon(
                painter = painterResource(id = imageId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding.dp)
                    .clickable { onClick() },
                tint = if (isSelected) actualTextColor else textColor
            )

        }
    }
}
