package com.itssagnikmukherjee.blueteauser.presentation.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp



object CustomColors{
    val blackest = Color(0xFF121111)
    val primaryBlack = Color(0xFF292526)
    val darkGray = Color(0xFF787676)
    val mediumGray = Color(0xFFA3A1A2)
    val lightGray = Color(0xFFF2F2F2)
}

@RequiresApi(Build.VERSION_CODES.O)
val headingTextStyle = TextStyle(
    fontSize = 20.sp,
    fontWeight = FontWeight.SemiBold,
    color = CustomColors.primaryBlack,
    fontFamily = fontFamily
)

