package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.binayshaw7777.kotstep.model.LineDefault
import com.binayshaw7777.kotstep.model.LineType
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconVerticalWithLabel
import com.binayshaw7777.kotstep.model.tabVerticalWithLabel
import com.binayshaw7777.kotstep.ui.vertical.VerticalStepper
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
    val transitTime = orderDetails["transitTime"] as? Long ?: 0L
    val deliveredTime = orderDetails["deliveredTime"] as? Long ?: 0L

    // State for pull-to-refresh
    val isRefreshing = viewModel.isRefreshing.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing.value)

    // Fetch user details on initial load
    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
    }

    // Pull-to-refresh functionality
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.refreshOrderDetails(userId) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = "Track Order", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Display order details
            Text(text = "Order ID: $orderId", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Status: $status", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Display stepper with timestamps
            StepIndicator(status, placedTime, transitTime, deliveredTime)

            Spacer(modifier = Modifier.height(16.dp))

            // Display user details
            val userDetails = getUserDetailsState.value.data
            userDetails?.let {
                Text(text = "User Details", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Name: ${it.firstName} ${it.lastName}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Email: ${it.email}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Phone: ${it.phoneNo}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Address: ${it.address}", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Button(onClick = { navController.popBackStack() }) {
                Text("Back to Orders")
            }
        }
    }
}

@Composable
fun StepIndicator(status: String, placedTime: Long, transitTime: Long, deliveredTime: Long) {
    // Explicitly define the type of `steps` as List<Pair<String, Long>>
    val steps: List<Pair<String, Long>> = listOf(
        "Order Placed" to placedTime,
        "In Transit" to (transitTime.takeIf { status == "In Transit" || status == "Delivered" } ?: 0L),
        "Delivered" to (deliveredTime.takeIf { status == "Delivered" } ?: 0L)
    )

    // Calculate the current step based on the status
    val currentStep = when (status) {
        "Delivered" -> 2 // Delivered is the final step
        "In Transit" -> 1 // In Transit is the second step
        else -> 0 // Default to the first step (Order Placed)
    }

    val customStepStyle = StepStyle(
        stepSize = 52.dp,
        stepShape = CircleShape,
        textSize = 16.sp,
        iconSize = 24.dp,
        stepPadding = 4.dp,
        lineStyle = LineDefault(
            lineSize = 70.dp,
            todoLineProgressType = LineType.DOTTED,
            currentLineProgressType = LineType.DOTTED,
            currentLineTrackType = LineType.DOTTED,
            progressStrokeCap = StrokeCap.Round
        ),
        showCheckMarkOnDone = true,
        showStrokeOnCurrent = false,
        colors = StepDefaults(
            todoContainerColor = Color.DarkGray,
            todoContentColor = Color.DarkGray,
            todoLineColor = Color.Gray,
            currentContainerColor = Color.Green,
            currentContentColor = Color.White,
            currentLineColor = Color.Gray,
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
                currentStep = currentStep, // Pass the correct currentStep
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