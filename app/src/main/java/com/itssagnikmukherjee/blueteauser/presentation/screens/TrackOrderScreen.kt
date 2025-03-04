package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconVerticalWithLabel
import com.binayshaw7777.kotstep.model.tabVerticalWithLabel
import com.binayshaw7777.kotstep.ui.vertical.VerticalStepper
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TrackOrderScreen(
    navController: NavController,
    orderId: String,
    userId: String,
    viewModel: ViewModels = hiltViewModel()
) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val orders = getUserDetailsState.value.data?.orderedItems as? Map<String, Map<String, Any>> ?: emptyMap()

    val orderDetails = orders[orderId] ?: emptyMap()

    val status = orderDetails["status"] as? String ?: "Pending"
    val placedTime = orderDetails["timestamp"] as? Long ?: 0L
    val transitTime = orderDetails["orderTransitTime"] as? Long ?: 0L
    val deliveredTime = orderDetails["orderDeliveredTime"] as? Long ?: 0L

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Track Order", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        StepIndicator(status, placedTime, transitTime, deliveredTime)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Orders")
        }
    }
}

@Composable
fun StepIndicator(status: String, placedTime: Long, transitTime: Long, deliveredTime: Long) {
    val steps = listOf(
        "Order Placed" to placedTime,
        "In Transit" to transitTime,
        "Delivered" to deliveredTime
    )

    val currentStep = when {
        deliveredTime > 0L -> 2
        transitTime > 0L -> 1
        else -> 0
    }

    val customStepStyle = StepStyle(
        stepSize = 52.dp,
        stepShape = CircleShape,
        textSize = 16.sp,
        iconSize = 24.dp,
        stepPadding = 4.dp,
        showCheckMarkOnDone = true,
        showStrokeOnCurrent = false,
        colors = StepDefaults(
            todoContainerColor = Color.DarkGray,
            todoContentColor = Color.DarkGray,
            todoLineColor = Color.Gray,
            currentContainerColor = Color.Green,
            currentContentColor = Color.White,
            currentLineColor = Color.Green,
            doneContainerColor = Color.Green,
            doneContentColor = Color.White,
            doneLineColor = Color.Green,
            checkMarkColor = Color.Black
        )
    )

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        VerticalStepper(
            style = iconVerticalWithLabel(
                stepStyle = customStepStyle,
                currentStep = 1,
                icons = listOf(
                    Icons.Default.CheckCircle,
                    Icons.Default.CheckCircle,
                    Icons.Default.CheckCircle
                ),
                trailingLabels = steps.map { (title, time) ->
                    {
                        Column {
                            Text(title)
                            if (time > 0L) {
                                Text(formatTimestamp(time), fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            )
        )
    }
}


fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}