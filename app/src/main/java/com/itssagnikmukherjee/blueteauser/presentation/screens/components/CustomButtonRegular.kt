package com.itssagnikmukherjee.blueteauser.presentation.screens.components
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

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