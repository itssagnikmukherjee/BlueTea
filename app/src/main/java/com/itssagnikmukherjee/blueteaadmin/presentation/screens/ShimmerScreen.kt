package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack

@Composable
fun ShimmerScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
            ShimmerElem()
    }
}

@Composable
fun ShimmerElem() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnim.value - 500, translateAnim.value - 500),
        end = Offset(translateAnim.value, translateAnim.value)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
            {
                Box(Modifier.clip(RoundedCornerShape(20.dp))) {
                    Box(modifier = Modifier
                        .background(brush)
                        .size(150.dp))
                }
                Column(
                    modifier = Modifier.height(150.dp).padding(10.dp),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                        Box(modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .background(brush))
                    }
                    Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                        Box(modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth()
                            .background(brush))
                    }
                    Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                        Box(modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth()
                            .background(brush))
                    }
                }
            }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
    {
        Box(Modifier.clip(RoundedCornerShape(20.dp))) {
            Box(modifier = Modifier
                .background(brush)
                .height(200.dp).fillMaxWidth())
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp))
    {
        Column(
            modifier = Modifier.height(150.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                Box(modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(brush))
            }
            Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                Box(modifier = Modifier
                    .height(30.dp)
                    .fillMaxWidth()
                    .background(brush))
            }
            Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                Box(modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth()
                    .background(brush))
            }
        }
    }

}