package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableChipColors
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.GetUserDetailsState
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomButton1
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomButtonFilled
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.HeadingTextWithBadge
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import kotlinx.serialization.json.Json

@Composable
fun OrdersScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    userId: String
) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val getProductsState = viewModel.getProductState.collectAsState()

    val allOrders =
        getUserDetailsState.value.data?.orderedItems as? Map<String, Map<String, Any>> ?: emptyMap()
    val products = getProductsState.value.data ?: emptyList()


    var selectedFilter by remember { mutableStateOf("Pending") }

    val filteredOrders = remember(allOrders, selectedFilter) {
        if (selectedFilter == "All") {
            allOrders
        } else {
            allOrders.filterValues { it["status"] == selectedFilter }
        }
    }

    val productMap = remember(products) { products.associateBy { it.productId } }

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
        viewModel.getProducts()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .fillMaxWidth(0.9f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = R.drawable.back,
                    contentDescription = "back"
                )
                HeadingTextWithBadge(
                    text = "Orders",
                    badgeText = filteredOrders.size.toString(),
                    width = 90
                )
                CustomIconButton(
                    onClick = { viewModel.getUserDetails(userId) },
                    icon = R.drawable.reload,
                    contentDescription = "back"
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val chipColor = SelectableChipColors(
                    labelColor = CustomColors.primaryBlack,
                    selectedContainerColor = CustomColors.darkGray,
                    selectedLabelColor = Color.White,
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    disabledLabelColor = Color.Gray,
                    disabledSelectedContainerColor = CustomColors.darkGray,
                    leadingIconColor = CustomColors.darkGray,
                    trailingIconColor = CustomColors.darkGray,
                    disabledLeadingIconColor = Color.Gray,
                    disabledTrailingIconColor = Color.Gray,
                    selectedLeadingIconColor = Color.White,
                    selectedTrailingIconColor = Color.White,
                )

                FilterChip(
                    selected = selectedFilter == "All",
                    onClick = { selectedFilter = "All" },
                    label = { Text("All", fontFamily = fontFamily) },
                    colors = chipColor
                )

                FilterChip(
                    selected = selectedFilter == "Pending",
                    onClick = { selectedFilter = "Pending" },
                    label = { Text("Pending", fontFamily = fontFamily) },
                    colors = chipColor
                )

                FilterChip(
                    selected = selectedFilter == "In Transit",
                    onClick = { selectedFilter = "In Transit" },
                    label = { Text("In Transit", fontFamily = fontFamily) },
                    colors = chipColor
                )

                FilterChip(
                    selected = selectedFilter == "Delivered",
                    onClick = { selectedFilter = "Delivered" },
                    label = { Text("Delivered", fontFamily = fontFamily) },
                    colors = chipColor
                )
            }

            if (filteredOrders.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredOrders.entries.toList().size) { order ->
                        OrderItemCard(
                            orderId = filteredOrders.entries.toList()[order].key,
                            orderDetails = filteredOrders.entries.toList()[order].value,
                            productMap = productMap,
                            userId = userId,
                            navController = navController
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = when (selectedFilter) {
                            "All" -> "No orders placed yet."
                            else -> "No $selectedFilter orders"
                        },
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun OrderItemCard(
    orderId: String,
    orderDetails: Map<String, Any>,
    productMap: Map<String, Product>,
    viewModel: ViewModels = hiltViewModel(),
    userId: String,
    navController: NavController
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 14.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
            val items = orderDetails["items"] as? Map<String, Long> ?: emptyMap()
            items.forEach { (productId, quantity) ->
                val product = productMap[productId]
                if (product != null) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                        verticalAlignment = Alignment.Top
                    ) {

                        AsyncImage(
                            model = product.productImages[0],
                            contentDescription = "",
                            modifier = Modifier
                                .size(130.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                        Spacer(Modifier.width(4.dp))

                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = product.productName,
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ){
                                    Row(
                                        verticalAlignment = Alignment.Top,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            "₹",
                                            fontFamily = fontFamily,
                                            color = primaryBlack,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 5.dp)
                                        )
                                        Text(
                                            text = "${product.productFinalPrice}",
                                            fontFamily = fontFamily,
                                            color = primaryBlack,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                Text(
                                    text = "X $quantity",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Light
                                )

                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                val price = product.productFinalPrice.toLong() * quantity
                                Text(
                                    text = "₹${price}",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                } else {
                    Text(text = "- Unknown Product ($productId) x $quantity")
                }
            }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("₹ ${orderDetails["totalPrice"]}", fontFamily = fontFamily, color = primaryBlack, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(start = 10.dp))
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${if (orderDetails["status"] == "Pending") "Ordered" else orderDetails["status"]}",
                    fontFamily = fontFamily,
                    color = primaryBlack,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = when (orderDetails["status"]) {
                        "Delivered" -> formatTimestamp(orderDetails["deliveredTime"] as Long)
                        "In Transit" -> formatTimestamp(orderDetails["transitTime"] as Long)
                        else -> formatTimestamp(orderDetails["timestamp"] as Long)
                    }, fontFamily = fontFamily, color = primaryBlack, fontSize = 16.sp
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomButton1(
                onclick = { showDialog = true },
                text = "Cancel",
            )

            Spacer(Modifier.width(10.dp))

            CustomButtonFilled(
                onclick = {
                    navController.navigate(Routes.TrackOrderScreen(orderId = orderId, userId = userId))
                },
                text = "Track"
            )
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

    Spacer(Modifier.height(30.dp))
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(
            thickness = 1.dp,
            color = CustomColors.mediumGray,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .align(Alignment.CenterVertically)
        )
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
        title = { Text("Cancel Order", fontWeight = FontWeight.SemiBold, fontSize = 26.sp, fontFamily = fontFamily) },
        text = {
            Column {
                Text("Are you sure you want to cancel this order ?", fontWeight = FontWeight.Normal, fontSize = 16.sp)
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
                                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(20.dp))
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(text = product.productName, fontWeight = FontWeight.Medium, fontFamily = fontFamily)
                                Text(text = "₹${product.productFinalPrice} x $quantity", fontWeight = FontWeight.Normal, fontFamily = fontFamily)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Total  ₹${orderDetails["totalPrice"]}", fontWeight = FontWeight.SemiBold, fontFamily = fontFamily, fontSize = 20.sp, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            CustomButtonFilled(onclick = onConfirm, text = "Yes cancel")
        },
        dismissButton = {
            CustomButton1(onclick = onDismiss, text = "Go Back")
        }
    )
}