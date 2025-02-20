package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.GetUserDetailsState
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    userId: String
) {

    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val getProductsState = viewModel.getProductState.collectAsState()

    val orders = getUserDetailsState.value.data?.orderedItems as? Map<String, Map<String, Any>> ?: emptyMap()
    val products = getProductsState.value.data ?: emptyList()

    val productMap = remember(products) { products.associateBy { it.productId } }

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
        viewModel.getProducts()
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Your Orders", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        if (orders.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(orders.entries.toList().size) { order ->
                    OrderItemCard(orderId = orders.entries.toList()[order].key, orderDetails = orders.entries.toList()[order].value, productMap = productMap, userId= userId)
                }
            }
        } else {
            Text(text = "No orders placed yet.", fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp))
        }
    }
}


@Composable
fun OrderItemCard(orderId: String, orderDetails: Map<String, Any>, productMap: Map<String, Product>, viewModel: ViewModels = hiltViewModel(), userId: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        var showDialog by remember { mutableStateOf(false) }
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Order ID: $orderId", fontWeight = FontWeight.Bold)
            Text(
                text = "Status: ${orderDetails["status"]}",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            val items = orderDetails["items"] as? Map<String, Long> ?: emptyMap()
            items.forEach { (productId, quantity) ->
                val product = productMap[productId]
                if (product != null) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = product.productImages[0],
                                contentDescription = product.productName,
                                modifier = Modifier.size(50.dp).padding(end = 8.dp)
                            )
                            Text(text = "- ${product.productName} x $quantity")
                            Text("₹${product.productFinalPrice}")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                } else {
                    Text(text = "- Unknown Product ($productId) x $quantity")
                }
            }
        }
        Text(text = "${orderDetails["paymentMethod"]}")
        Text(text = "${orderDetails["totalPrice"]}")
        Button(onClick = {}) {
            Text("Track Order")
        }
        Button(onClick = {
            showDialog = true
        }) {
            Text("Cancel Order")
        }
        if (showDialog) {
            CancelOrderDialog(
                orderId = orderId,
                orderDetails = orderDetails,
                onDismiss = { showDialog = false },
                onConfirm = {
                    viewModel.cancelOrder(userId, orderId)
                    showDialog = false
                },
                productMap = productMap
            )
        }
    }
}

@Composable
fun CancelOrderDialog(
    orderId: String,
    orderDetails: Map<String, Any>,
    productMap: Map<String, Product>,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cancel Order") },
        text = {
            Column {
                Text("Are you sure you want to cancel this order?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                val items = orderDetails["items"] as? Map<String, Long> ?: emptyMap()

                items.forEach { (productId, quantity) ->
                    val product = productMap[productId]
                    if (product != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = product.productImages[0],
                                contentDescription = product.productName,
                                modifier = Modifier.size(50.dp).padding(end = 8.dp)
                            )
                            Column {
                                Text(text = product.productName, fontWeight = FontWeight.Bold)
                                Text(text = "₹${product.productFinalPrice} x $quantity")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Price: ₹${orderDetails["totalPrice"]}", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Yes, Cancel")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No, Keep It")
            }
        }
    )
}