package com.itssagnikmukherjee.blueteauser.presentation.screens.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors

@Composable
fun CustomActionButton(onClick: () -> Unit, icon: Int, contentDescription: String, modifier: Modifier) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = CustomColors.primaryBlack,
            contentColor = Color.White
        ),
        modifier = Modifier.size(30.dp)
    ) {
        Icon(painter = painterResource(icon), contentDescription = contentDescription, modifier = Modifier.size(15.dp))
    }
}

@Composable
fun CustomIconButton(onClick: () -> Unit, icon: Int, contentDescription: String, modifier: Modifier = Modifier, color: Color = CustomColors.darkGray) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = color,
            contentColor = Color.White
        ),
        modifier = Modifier.size(48.dp)
    ) {
        Icon(painter = painterResource(icon), contentDescription = contentDescription, modifier = Modifier.size(16.dp))
    }
}
