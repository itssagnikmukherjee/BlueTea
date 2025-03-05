package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteaadmin.domain.models.OrderDetails
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.domain.models.UserData
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
                    Text(
                        text = "Order Date: ${userDetails?.orderedItems?.keys}",
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
                    if(currentStatus == "Delivered") Text("Delivered on ${orderDetail?.get("deliveredTime")}")else{
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