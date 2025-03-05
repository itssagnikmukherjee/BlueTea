package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.binayshaw7777.kotstep.model.LineDefault
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconHorizontal
import com.binayshaw7777.kotstep.model.numberedHorizontal
import com.binayshaw7777.kotstep.ui.horizontal.HorizontalStepper
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersScreen(viewModel: ViewModels = hiltViewModel()) {
    val orderDetailsState = viewModel.orderDetailsState.collectAsState()
    val getProductState = viewModel.getProductState.collectAsState()
    val userDetailsState = viewModel.getUserDetailsState.collectAsState()

    val orders = orderDetailsState.value.data ?: emptyList()
    val products = getProductState.value.data ?: emptyList()

    val allActiveOrderUserIds = remember(orders) { orders.map { it.userId }.distinct() }
    val productMap = remember(products) { products.associateBy { it.productId } }

    LaunchedEffect(allActiveOrderUserIds) {
        allActiveOrderUserIds.forEach { userId ->
            viewModel.getUserDetails(userId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.getOrderDetails()
        viewModel.getProducts()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Iterate over users
        items(allActiveOrderUserIds) { userId ->
            val userDetails = userDetailsState.value.data
            val orderedItems = userDetails?.orderedItems ?: emptyMap()

            // Iterate over orders for the user
            orderedItems.forEach { (orderId, orderDetail) ->
                // State to manage card expansion
                var isExpanded by remember { mutableStateOf(false) }

                // Extract timestamps
                val inTransitTimestamp = orderDetail["transitTime"] as? Long
                val deliveredTimestamp = orderDetail["deliveredTime"] as? Long

                // Format timestamps into date and time
                val orderDate = orderDetail["timestamp"]?.let { formatDate(it as Long) } ?: "N/A"
                val orderTime = orderDetail["timestamp"]?.let { formatTime(it as Long) } ?: "N/A"
                val deliveredDate = deliveredTimestamp?.let { formatDate(it) } ?: "N/A"
                val deliveredTime = deliveredTimestamp?.let { formatTime(it) } ?: "N/A"
                val inTransitDate = inTransitTimestamp?.let { formatDate(it) } ?: "N/A"
                val inTransitTime = inTransitTimestamp?.let { formatTime(it) } ?: "N/A"

                // Card for each order
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Display order ID
                        Text("Order ID: $orderId", style = MaterialTheme.typography.bodyLarge)

                        // Display items in the order
                        val itemsMap = orderDetail["items"] as? Map<String, Int> ?: emptyMap()
                        itemsMap.forEach { (productId, quantity) ->
                            val product = productMap[productId]
                            if (product != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    AsyncImage(
                                        model = product.productImages[0],
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(MaterialTheme.shapes.medium)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = product.productName,
                                            style = MaterialTheme.typography.bodyLarge
                                        )

                                        Text(
                                            text = "Quantity: $quantity",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = "Price: ${product.productFinalPrice}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        // Expand/collapse icon button
                        IconButton(onClick = { isExpanded = !isExpanded }) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = if (isExpanded) "Collapse" else "Expand"
                            )
                        }

                        // Order date and payment method
                        Text(
                            text = "Order Date: $orderDate",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Text(
                            text = "Payment Method: ${orderDetail["paymentMethod"]}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Display the current status and a button to change it
                        val currentStatus = orderDetail["status"] as? String ?: "N/A"
                        var showStatusDialog by remember { mutableStateOf(false) }

                        if (currentStatus == "Delivered") {
                            Text("Delivered on $deliveredDate @ $deliveredTime")
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Text(
                                    text = "Status:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Button(onClick = { showStatusDialog = true }) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(currentStatus)
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Dialog to change the status
                        if (showStatusDialog) {
                            val statusOptions = when (currentStatus) {
                                "Pending" -> listOf("In Transit", "Cancelled")
                                "In Transit" -> listOf("Delivered", "Cancelled")
                                else -> emptyList() // No options for "Delivered" or "Cancelled"
                            }

                            AlertDialog(
                                onDismissRequest = { showStatusDialog = false },
                                title = { Text("Change Status") },
                                text = {
                                    Column {
                                        statusOptions.forEach { status ->
                                            TextButton(
                                                onClick = {
                                                    // Update the status in the backend
                                                    viewModel.updateOrderStatus(
                                                        userId = userId, // Pass the user ID
                                                        orderId = orderId, // Pass the order ID
                                                        newStatus = status // Pass the new status
                                                    )
                                                    showStatusDialog = false
                                                },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(status)
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showStatusDialog = false }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }

                        // Expanded section (visible when expanded)
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                // User details
                                Text(
                                    text = "User ID: $userId",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Text(
                                    text = "${userDetails?.firstName} ${userDetails?.lastName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                Text(
                                    text = userDetails?.email.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = userDetails?.phoneNo.toString(),
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = userDetails?.address.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Stepper for order status
                                val currentStep = when (currentStatus) {
                                    "Pending" -> 1
                                    "In Transit" -> 2
                                    else -> 3
                                }

                                val customStepStyle = StepStyle(
                                    stepSize = 50.dp,
                                    stepShape = CircleShape,
                                    textSize = 16.sp,
                                    iconSize = 24.dp,
                                    lineStyle = LineDefault(
                                        lineSize = 70.dp
                                    ),
                                    stepPadding = 2.dp,
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

                                HorizontalStepper(
                                    style = iconHorizontal(
                                        stepStyle = customStepStyle,
                                        currentStep = currentStep,
                                        icons = listOf(
                                            Icons.Default.CheckCircle,
                                            Icons.Default.CheckCircle,
                                            Icons.Default.CheckCircle
                                        )
                                    )
                                )

                                // Display status and corresponding date/time
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Ordered Section (Visible if currentStep >= 1)
                                    if (currentStep >= 1) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text("Ordered", style = MaterialTheme.typography.bodyMedium)
                                                Text("Date: $orderDate", style = MaterialTheme.typography.bodySmall)
                                                Text("Time: $orderTime", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }

                                    // In Transit Section (Visible if currentStep >= 2)
                                    if (currentStep >= 2) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.Start
                                            ) {
                                                Text("In Transit", style = MaterialTheme.typography.bodyMedium)
                                                Text("Date: $inTransitDate", style = MaterialTheme.typography.bodySmall)
                                                Text("Time: $inTransitTime", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }

                                    // Delivered Section (Visible if currentStep >= 3)
                                    if (currentStep >= 3) {
                                        Box(modifier = Modifier.weight(1f)) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                Text("Delivered", style = MaterialTheme.typography.bodyMedium)
                                                Text("Date: $deliveredDate", style = MaterialTheme.typography.bodySmall)
                                                Text("Time: $deliveredTime", style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("hh:mm:ss a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}