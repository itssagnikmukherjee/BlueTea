package com.itssagnikmukherjee.blueteauser.presentation.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

@Composable
fun HeadingTextWithBadge(text: String, badgeText: String, width: Int) {
    Box(
        modifier = Modifier.width(width.dp)
    ){
    Text(text = text, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = CustomColors.primaryBlack,
        fontFamily = fontFamily
    )
        Box(modifier = Modifier.align(
            Alignment.TopEnd).clip(CircleShape).background(CustomColors.darkGray).size(15.dp), contentAlignment = Alignment.Center){
    Text(text = badgeText, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
        }
    }
}
