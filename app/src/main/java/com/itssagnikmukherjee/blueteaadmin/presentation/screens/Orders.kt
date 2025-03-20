package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.itssagnikmukherjee.blueteaadmin.R
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersScreen(viewModel: ViewModels = hiltViewModel()) {
    val orderDetailsState by viewModel.orderDetailsState.collectAsState()
    val userDetailsMap by viewModel.userDetailsMap.collectAsState()
    val productDetailsMap by viewModel.productDetailsMap.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getOrderDetails()
    }

    val orders = orderDetailsState.data ?: emptyList()

    Box(modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp))) {
        when {
            orderDetailsState.isLoading -> {
//                Box(modifier = Modifier.size(200.dp).shimmer().background(Color.Black).fillMaxSize())
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            orderDetailsState.error != null -> {
                Text(
                    text = "Error: ${orderDetailsState.error}",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )
            }
            orders.isEmpty() -> {
                Text(
                    text = "No orders available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                Column {
                    OrderFilterChips()
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(orders) { order ->
                            val userDetails = userDetailsMap[order.userId]
                            var isExpanded by remember { mutableStateOf(false) }

                            //date time stamps
                            val inTransitTimestamp = order.transitTime
                            val deliveredTimestamp = order.deliveredTime

                            val deliveredDate = formatDate(deliveredTimestamp)
                            val deliveredTime = formatTime(deliveredTimestamp)
                            val inTransitDate = formatDate(inTransitTimestamp)
                            val inTransitTime = formatTime(inTransitTimestamp)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().background(Color.Cyan).padding(horizontal = 20.dp, vertical = 2.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "#${order.orderId}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = order.status,
                                            fontSize = 14.sp,
                                        )
                                        IconButton(
                                            onClick = { isExpanded = !isExpanded }
                                        ) {
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = if (isExpanded) "Collapse" else "Expand"
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    order.items.forEach { (productId, quantity) ->
                                        val productData = productDetailsMap[productId]

                                        if (productData != null) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AsyncImage(
                                                    model = productData.productImages[0],
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(100.dp)
                                                        .clip(MaterialTheme.shapes.medium)
                                                )

                                                Spacer(modifier = Modifier.width(16.dp))

                                                // Product Details
                                                Column(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Text(
                                                        text = productData.productName,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
                                                    Text(
                                                        text = "₹${productData.productFinalPrice}",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }

                                                // Quantity
                                                Text(
                                                    text = "x $quantity",
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }

                                            // Subtotal
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                Text(
                                                    text = "Subtotal  ₹${productData.productFinalPrice * quantity}",
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        } else {
                                            Text(
                                                text = "Loading product details...",
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                                    if (userDetails != null) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = "${userDetails.firstName} ${userDetails.lastName} | ${userDetails.phoneNo}",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            Text(
                                                text = userDetails.email,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "Loading user details...",
                                            color = Color.Gray
                                        )
                                    }

                                    // Order Total (if multiple products)
                                    if (order.items.size > 1) {
                                        val total = order.items.entries.sumOf { (productId, quantity) ->
                                            productDetailsMap[productId]?.productFinalPrice?.times(
                                                quantity
                                            )
                                                ?: 0
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "Order Total: ₹$total",
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    AnimatedVisibility(visible = isExpanded) {
                                        Column(
                                            modifier = Modifier.padding(top = 16.dp)
                                        ) {
                                            // Stepper
                                            val currentStep = when (order.status) {
                                                "Pending" -> 1
                                                "In Transit" -> 2
                                                "Delivered" -> 3
                                                else -> 1
                                            }

                                            val customStepStyle = StepStyle(
                                                stepSize = 50.dp,
                                                stepShape = CircleShape,
                                                textSize = 16.sp,
                                                iconSize = 24.dp,
                                                lineStyle = LineDefault(lineSize = 70.dp),
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

                                            // Date and Times
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.Start
                                                ) {
                                                    Text(
                                                        text = "Ordered",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    Text(
                                                        text = formatDate(order.timestamp),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        text = formatTime(order.timestamp),
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }

                                                if (currentStep >= 2) {
                                                    Column(
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        Text(
                                                            text = "In Transit",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                        Text(
                                                            text = inTransitDate,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                        Text(
                                                            text = inTransitTime,
                                                            style = MaterialTheme.typography.bodySmall
                                                        )
                                                    }
                                                }

                                                if (currentStep >= 3) {
                                                    Column(
                                                        horizontalAlignment = Alignment.End
                                                    ) {
                                                        Text(
                                                            text = "Delivered",
                                                            style = MaterialTheme.typography.bodyMedium
                                                        )
                                                    Text(
                                                        text = deliveredDate,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    Text(
                                                        text = deliveredTime,
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    val currentStatus = order.status
                                    var showStatusDialog by remember { mutableStateOf(false) }

                                    if (currentStatus == "Delivered") {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(2.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                        ){
                                            Text("Delivered  ", fontSize = 14.sp, color = Color.Gray)
                                            Icon(painter = painterResource(R.drawable.calendar),"", modifier = Modifier.size(12.dp), tint = Color.Gray)
                                      Text("  $deliveredDate  ", fontSize = 14.sp, color = Color.Gray)
                                            Icon(painter = painterResource(R.drawable.clock_solid),"", modifier = Modifier.size(12.dp), tint = Color.Gray)
                                      Text("  $deliveredTime", fontSize = 14.sp, color = Color.Gray)
                                        }
                                    } else {
                                        CustomStatusButton(
                                            modifier = Modifier.padding(top=10.dp),
                                            status = currentStatus,
                                            onClick = { showStatusDialog = true }
                                        )
                                    }

                                    if (showStatusDialog) {
                                        val statusOptions = when (currentStatus) {
                                            "Pending" -> listOf("In Transit", "Cancelled")
                                            "In Transit" -> listOf("Delivered", "Cancelled")
                                            else -> emptyList()
                                        }

                                        AlertDialog(
                                            onDismissRequest = { showStatusDialog = false },
                                            title = { Text("Change Status", fontFamily = fontFamily) },
                                            text = {
                                                Column {
                                                    statusOptions.forEach { status ->
                                                        Text(status,
                                                            modifier = Modifier.fillMaxWidth().padding(20.dp).clickable{
                                                                viewModel.updateOrderStatus(
                                                                    userId = order.userId,
                                                                    orderId = order.orderId,
                                                                    newStatus = status
                                                                )
                                                                showStatusDialog = false
                                                            })
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

                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFilterChips() {

    var selectedChip by remember { mutableStateOf("All") }

    val chips = listOf("All", "In Transit", "Ordered", "Delivered", "Cancelled", "Refunded") // Added more chips for demonstration

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "Orders",
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { chip ->
                FilterChip(
                    selected = (chip == selectedChip),
                    onClick = { selectedChip = chip },
                    label = {
                        Text(text = chip)
                    },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timestamp: Long?): String {
    return timestamp?.let {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(formatter)
    } ?: "N/A"
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatTime(timestamp: Long?): String {
    return timestamp?.let {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(formatter)
    } ?: "N/A"
}

@Composable
fun CustomStatusButton(
    status: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = status,
                fontFamily = fontFamily,
                fontSize = 16.sp
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}