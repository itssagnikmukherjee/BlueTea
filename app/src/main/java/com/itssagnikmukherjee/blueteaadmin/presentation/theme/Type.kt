package com.itssagnikmukherjee.blueteaadmin.presentation.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itssagnikmukherjee.blueteaadmin.R


val fontFamily = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold)
)

@Composable
fun CustomButton1(onclick : () -> Unit, text: String, width: Int = 120) {
    Button(onClick = onclick, colors = ButtonDefaults.buttonColors(
        containerColor = _root_ide_package_.androidx.compose.ui.graphics.Color.Transparent,
        contentColor = CustomColors.primaryBlack
    ), modifier = Modifier.border(2.dp, CustomColors.primaryBlack, RoundedCornerShape(30.dp)).height(35.dp).width(width.dp)) {
        Text(text = text, color = CustomColors.primaryBlack, fontFamily = fontFamily, fontSize = 12.sp)
    }
}

@Composable
fun CustomButtonFilled(onclick : () -> Unit, text: String, width: Int = 120) {
    Button(onClick = onclick, colors = ButtonDefaults.buttonColors(
        containerColor = CustomColors.primaryBlack,
        contentColor = _root_ide_package_.androidx.compose.ui.graphics.Color.White
    ), modifier = Modifier.border(2.dp, CustomColors.primaryBlack, RoundedCornerShape(30.dp)).height(35.dp).width(width.dp)) {
        Text(text = text, color = _root_ide_package_.androidx.compose.ui.graphics.Color.White, fontFamily = fontFamily, fontSize = 12.sp)
    }
}

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)