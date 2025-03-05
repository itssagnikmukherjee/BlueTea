package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
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
        items(allActiveOrderUserIds) { userId ->
            val userDetails = userDetailsState.value.data
            val orderDetail = userDetails?.orderedItems?.values?.firstOrNull()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Extract and format the order date
                    val orderTimestamp = userDetails?.orderedItems?.keys?.firstOrNull()
                    val orderDate = orderTimestamp?.let { time(it.toLong()) } ?: "Invalid Date"

                    Text(
                        text = "Order Date: $orderDate",
                        style = MaterialTheme.typography.bodyMedium
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

                    Text(
                        text = "Payment Method: ${orderDetail?.get("paymentMethod")}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Display the current status and a button to change it
                    val currentStatus = orderDetail?.get("status") as? String ?: "N/A"
                    var showStatusDialog by remember { mutableStateOf(false) }

                    if (currentStatus == "Delivered") {
                        Text("Delivered on ${time(orderDetail?.get("deliveredTime") as Long)}")
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
                                                viewModel.updateOrderStatus(userId, status)
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

                    // Display ordered items
                    userDetails?.orderedItems?.forEach { order ->
                        val itemsMap = order.value["items"] as? Map<String, Int> ?: emptyMap()
                        itemsMap.forEach { (productId, quantity) ->
                            val product = productMap[productId]
                            if (product != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
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
                                            text = "Price: ${product.productFinalPrice}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = "Quantity: $quantity",
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = "Total Price: ${product.productFinalPrice?.times(quantity)}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
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
fun time(t: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy @ hh:mm:ss a")
        .withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(t))
}