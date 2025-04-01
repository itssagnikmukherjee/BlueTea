package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.binayshaw7777.kotstep.model.LineDefault
import com.binayshaw7777.kotstep.model.LineType
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconHorizontal
import com.binayshaw7777.kotstep.model.numberedHorizontal
import com.binayshaw7777.kotstep.ui.horizontal.HorizontalStepper
import com.itssagnikmukherjee.blueteaadmin.R
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.lightBackgroundColor
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OrdersScreen(viewModel: ViewModels = hiltViewModel()) {
    val orderDetailsState by viewModel.orderDetailsState.collectAsState()
    val userDetailsMap by viewModel.userDetailsMap.collectAsState()
    val productDetailsMap by viewModel.productDetailsMap.collectAsState()
    val selectedFilters = remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        viewModel.getOrderDetails()
    }

    val orders = orderDetailsState.data ?: emptyList()

    val filteredOrders = remember(orders, selectedFilters.value) {
        if (selectedFilters.value.isEmpty()) {
            orders
        } else {
            orders.filter { order ->
                selectedFilters.value.contains(order.status)
            }
        }
    }
    val coroutineScope = rememberCoroutineScope()
    Column {
        val totalPendingOrdersCount = orders.count { it.status == "Pending" }
        val totalInTransitOrdersCount = orders.count { it.status == "In Transit" }
        val totalDeliveredOrdersCount = orders.count { it.status == "Delivered" }

        OrderFilterChips(
            totalOrders = orders.size,
            selectedFilters = selectedFilters.value,
            onFilterSelected = { filter ->
                selectedFilters.value = if (selectedFilters.value.contains(filter)) {
                    selectedFilters.value - filter
                } else {
                    selectedFilters.value + filter
                }
            },

        onRefreshClick = {
            coroutineScope.launch {
                viewModel.getOrderDetails()
            }
        },
        pendingCount = totalPendingOrdersCount,
            inTransitCount = totalInTransitOrdersCount,
            deliveredCount = totalDeliveredOrdersCount
        )

        Box(modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))) {
            when {
                orderDetailsState.isLoading -> {
                    ShimmerScreen()
                }
                orderDetailsState.error != null -> {
                    Text(
                        text = "Error: ${orderDetailsState.error}",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }
                filteredOrders.isEmpty() -> {
                    Text(
                        text = if (orders.isEmpty()) "No orders available" else{
                            "No orders match the selected filters" },
                        modifier = Modifier.align(Alignment.Center),
                        fontFamily = fontFamily,
                    )
                }
                else -> {
                    Column {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredOrders.reversed()) { order ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = slideInVertically(initialOffsetY = {it}) + fadeIn(),
                                    exit = slideOutVertically() + fadeOut()
                                ) {
                                    val userDetails = userDetailsMap[order.userId]
                                    var isExpanded by remember { mutableStateOf(false) }
                                    val rotationState by animateFloatAsState(
                                        targetValue = if (isExpanded) 180f else 0f,
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutSlowInEasing
                                        ),
                                        label = "expandRotation"
                                    )

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
                                        colors = CardDefaults.cardColors(
                                            containerColor = CustomColors.lightGray
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(primaryBlack)
                                                .padding(horizontal = 20.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = "#${order.orderId}",
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                AnimatedVisibility(
                                                    visible = true,
                                                    enter = slideInHorizontally(
                                                        initialOffsetX = { it },
                                                        animationSpec = tween(durationMillis = 500)
                                                    ) + fadeIn(),
                                                    exit = slideOutHorizontally() + fadeOut()
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            painter = painterResource(
                                                                when (order.status) {
                                                                    "Delivered" -> R.drawable.truck_solid
                                                                    "In Transit" -> R.drawable.transit
                                                                    else -> R.drawable.pending
                                                                }
                                                            ),
                                                            contentDescription = null,
                                                            modifier = Modifier.size(12.dp),
                                                            tint = Color.White
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = order.status,
                                                            fontSize = 12.sp,
                                                            color = Color.White
                                                        )
                                                    }
                                                }

                                                IconButton(onClick = {
                                                    isExpanded = !isExpanded
                                                }) {
                                                    Icon(
                                                        imageVector = Icons.Default.KeyboardArrowDown,
                                                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                                                        tint = Color.White,
                                                        modifier = Modifier.rotate(rotationState)
                                                    )
                                                }
                                            }
                                        }

                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            order.items.forEach { (productId, quantity) ->
                                                val productData = productDetailsMap[productId]

                                                if (productData != null) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(bottom = 10.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        AsyncImage(
                                                            model = productData.productImages[0],
                                                            contentDescription = null,
                                                            modifier = Modifier
                                                                .size(120.dp)
                                                                .clip(MaterialTheme.shapes.medium)
                                                        )

                                                        Spacer(modifier = Modifier.width(16.dp))

                                                        Column(
                                                            modifier = Modifier.height(120.dp),
                                                            verticalArrangement = Arrangement.SpaceBetween
                                                        ) {
                                                            Row {
                                                                Column(
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .align(Alignment.Top)
                                                                ) {
                                                                    Text(
                                                                        text = productData.productName,
                                                                        fontFamily = fontFamily,
                                                                        fontSize = 18.sp,
                                                                        color = primaryBlack
                                                                    )
                                                                    Row(
                                                                        modifier = Modifier.fillMaxWidth(),
                                                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                                        verticalAlignment = Alignment.CenterVertically
                                                                    ) {
                                                                        Text(
                                                                            text = "₹${productData.productFinalPrice}",
                                                                            fontFamily = fontFamily,
                                                                            fontSize = 20.sp,
                                                                            color = primaryBlack,
                                                                            fontWeight = FontWeight.Normal
                                                                        )
                                                                        Text(
                                                                            text = "x $quantity",
                                                                            fontSize = 20.sp,
                                                                            fontWeight = FontWeight.Normal,
                                                                            color = primaryBlack
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                            Row(
                                                                modifier = Modifier.fillMaxWidth(),
                                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                if (order.items.size > 1) Text(
                                                                    "Subtotal",
                                                                    color = primaryBlack
                                                                ) else Text(
                                                                    "Total",
                                                                    color = primaryBlack
                                                                )
                                                                Text(
                                                                    text = "₹${productData.productFinalPrice * quantity}",
                                                                    fontWeight = FontWeight.Bold,
                                                                    color = primaryBlack,
                                                                    fontSize = 26.sp
                                                                )
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Text(
                                                        text = "Loading product details...",
                                                        color = Color.Gray
                                                    )
                                                }
                                            }

                                            if (order.items.size > 1) {
                                                Divider(
                                                    Modifier
                                                        .padding(vertical = 8.dp)
                                                        .fillMaxWidth()
                                                        .background(
                                                            Color.Gray
                                                        )
                                                )
                                                val total =
                                                    order.items.entries.sumOf { (productId, quantity) ->
                                                        productDetailsMap[productId]?.productFinalPrice?.times(
                                                            quantity
                                                        )
                                                            ?: 0
                                                    }
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text("Order Total")
                                                    Text(
                                                        text = "₹$total",
                                                        fontSize = 28.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = primaryBlack,
                                                    )
                                                }
                                            }

                                            if (userDetails != null) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(95.dp)
                                                ) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(CustomColors.lightGray)
                                                            .zIndex(1f)
                                                            .padding(end = 10.dp)
                                                    ) {
                                                        Text(
                                                            text = "#${userDetails.userId}",
                                                            fontSize = 12.sp,
                                                            color = Color.Gray,
                                                        )
                                                    }


                                                    Box(
                                                        modifier = Modifier
                                                            .border(
                                                                1.dp,
                                                                Color.Gray,
                                                                RoundedCornerShape(12.dp)
                                                            )
                                                            .fillMaxWidth()
                                                            .padding(16.dp)
                                                            .align(Alignment.BottomEnd)
                                                    ) {
                                                        Column {
                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                               val userName = userDetails.firstName.replaceFirstChar{it.uppercase()} + " " + userDetails.lastName.replaceFirstChar{it.uppercase()}
                                                                Text(
                                                                    text = userName,
                                                                    fontWeight = FontWeight.Medium,
                                                                    fontSize = 16.sp,
                                                                    color = primaryBlack,
                                                                    modifier = Modifier.weight(1f)
                                                                )
                                                                Icon(
                                                                    imageVector = Icons.Default.Phone,
                                                                    contentDescription = "Phone Icon",
                                                                    tint = primaryBlack,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(
                                                                    text = userDetails.phoneNo,
                                                                    fontSize = 16.sp,
                                                                    color = primaryBlack
                                                                )
                                                            }

                                                            Spacer(modifier = Modifier.height(2.dp))

                                                            Row(
                                                                verticalAlignment = Alignment.CenterVertically
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Email,
                                                                    contentDescription = "Email Icon",
                                                                    tint = Color.Gray,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                                Spacer(modifier = Modifier.width(4.dp))
                                                                Text(
                                                                    text = userDetails.email,
                                                                    fontSize = 12.sp,
                                                                    color = Color.Gray
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                Text(
                                                    text = "Loading user details...",
                                                    color = Color.Gray
                                                )
                                            }

                                            AnimatedVisibility(
                                                visible = isExpanded,
                                                enter = expandVertically(
                                                    animationSpec = tween(durationMillis = 300)
                                                ),
                                                exit = shrinkVertically(
                                                    animationSpec = tween(durationMillis = 300)
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(top = 16.dp)
                                                ) {
                                                    // Stepper
                                                    val currentStep = when (order.status) {
                                                        "Pending" -> 0
                                                        "In Transit" -> 1
                                                        "Delivered" -> 2
                                                        else -> 0
                                                    }

                                                    val customStepStyle = StepStyle(
                                                        colors = StepDefaults(
                                                            todoContainerColor = CustomColors.darkGray,
                                                            todoContentColor = CustomColors.mediumGray,
                                                            currentContainerColor = CustomColors.primaryBlack,
                                                            currentContentColor = Color.White,
                                                            currentLineColor = CustomColors.darkGray,
                                                            doneContainerColor = CustomColors.primaryBlack,
                                                            doneContentColor = Color.White,
                                                            doneLineColor = primaryBlack,
                                                            checkMarkColor = Color.White,
                                                        ),
                                                        lineStyle = LineDefault(
                                                            lineThickness = 5.dp,
                                                            lineSize = 60.dp,
                                                            linePaddingStart = 0.dp,
                                                            linePaddingEnd = 0.dp,
                                                            linePaddingTop = 0.dp,
                                                            linePaddingBottom = 0.dp,
                                                            trackStrokeCap = StrokeCap.Round,
                                                            progressStrokeCap = StrokeCap.Round,
                                                            todoLineTrackType = LineType.DOTTED,
                                                            todoLineProgressType = LineType.DOTTED,
                                                            currentLineTrackType = LineType.DOTTED,
                                                            currentLineProgressType = LineType.DOTTED,
                                                            doneLineTrackType = LineType.SOLID,
                                                            doneLineProgressType = LineType.SOLID,
                                                        ),
                                                        stepSize = 60.dp,
                                                        stepShape = CircleShape,
                                                        stepStroke = 3f,
                                                        textSize = 16.sp,
                                                        iconSize = 24.dp,
                                                        stepPadding = 0.dp,
                                                        showCheckMarkOnDone = false,
                                                        showStrokeOnCurrent = true,
                                                        ignoreCurrentState = true
                                                    )

                                                    HorizontalStepper(
                                                        style = iconHorizontal(
                                                            stepStyle = customStepStyle,
                                                            currentStep = currentStep,
                                                            icons = listOf(
                                                                Icons.Default.AppShortcut,
                                                                Icons.Default.AccessTimeFilled,
                                                                Icons.Default.LocalShipping
                                                            )
                                                        )
                                                    )

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(top = 6.dp),
                                                        horizontalArrangement = Arrangement.Start
                                                    ) {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            modifier = Modifier.padding(horizontal = 20.dp)
                                                        ) {
                                                            Text(
                                                                text = "Ordered",
                                                                fontFamily = fontFamily,
                                                                fontSize = 16.sp,
                                                                color = primaryBlack,
                                                                fontWeight = FontWeight.Medium

                                                            )
                                                            Text(
                                                                text = formatDate(order.timestamp),
                                                                fontFamily = fontFamily,
                                                                fontSize = 12.sp,
                                                                color = primaryBlack

                                                            )
                                                            Text(
                                                                text = formatTime(order.timestamp),
                                                                fontFamily = fontFamily,
                                                                fontSize = 12.sp,
                                                                color = primaryBlack

                                                            )
                                                        }

                                                        if (currentStep >= 1) {
                                                            Column(
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                modifier = Modifier.padding(
                                                                    horizontal = 20.dp
                                                                )
                                                            ) {
                                                                Text(
                                                                    text = "In Transit",
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 16.sp,
                                                                    color = primaryBlack,
                                                                    fontWeight = FontWeight.Medium

                                                                )
                                                                Text(
                                                                    text = inTransitDate,
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 12.sp,
                                                                    color = primaryBlack

                                                                )
                                                                Text(
                                                                    text = inTransitTime,
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 12.sp,
                                                                    color = primaryBlack

                                                                )
                                                            }
                                                        }

                                                        if (currentStep >= 2) {
                                                            Column(
                                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                                modifier = Modifier.padding(
                                                                    horizontal = 16.dp
                                                                )
                                                            ) {
                                                                Text(
                                                                    text = "Delivered",
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 16.sp,
                                                                    color = primaryBlack,
                                                                    fontWeight = FontWeight.Medium,
                                                                )
                                                                Text(
                                                                    text = deliveredDate,
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 12.sp,
                                                                    color = primaryBlack

                                                                )
                                                                Text(
                                                                    text = deliveredTime,
                                                                    fontFamily = fontFamily,
                                                                    fontSize = 12.sp,
                                                                    color = primaryBlack

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
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center,
                                                ) {
                                                    Text(
                                                        "Delivered  ",
                                                        fontSize = 14.sp,
                                                        color = Color.Gray
                                                    )
                                                    Icon(
                                                        painter = painterResource(R.drawable.calendar),
                                                        "",
                                                        modifier = Modifier.size(12.dp),
                                                        tint = Color.Gray
                                                    )
                                                    Text(
                                                        "  $deliveredDate  ",
                                                        fontSize = 14.sp,
                                                        color = Color.Gray
                                                    )
                                                    Icon(
                                                        painter = painterResource(R.drawable.clock_solid),
                                                        "",
                                                        modifier = Modifier.size(12.dp),
                                                        tint = Color.Gray
                                                    )
                                                    Text(
                                                        "  $deliveredTime",
                                                        fontSize = 14.sp,
                                                        color = Color.Gray
                                                    )
                                                }
                                            } else {
                                                CustomStatusButton(
                                                    modifier = Modifier.padding(top = 15.dp),
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
                                                    title = {
                                                        Text(
                                                            "Change Status",
                                                            fontFamily = fontFamily,
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    },
                                                    text = {
                                                        Column(
                                                            horizontalAlignment = Alignment.CenterHorizontally,
                                                            verticalArrangement = Arrangement.Center
                                                        ) {
                                                            statusOptions.forEach { status ->
                                                                Box(
                                                                    modifier = Modifier
                                                                        .clip(
                                                                            RoundedCornerShape(10.dp)
                                                                        )
                                                                        .clickable {
                                                                            viewModel.updateOrderStatus(
                                                                                userId = order.userId,
                                                                                orderId = order.orderId,
                                                                                newStatus = status
                                                                            )
                                                                            showStatusDialog = false
                                                                        },
                                                                ) {
                                                                    Row(
                                                                        modifier = Modifier
                                                                            .fillMaxWidth()
                                                                            .padding(horizontal = 20.dp),
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                    ) {
                                                                        Icon(
                                                                            painter = painterResource(
                                                                                when (status) {
                                                                                    "Delivered" -> R.drawable.truck_solid
                                                                                    "In Transit" -> R.drawable.transit
                                                                                    "Cancelled" -> R.drawable.xmark_solid
                                                                                    else -> R.drawable.pending
                                                                                }
                                                                            ),
                                                                            "",
                                                                            modifier = Modifier.size(
                                                                                20.dp
                                                                            )
                                                                        )
                                                                        Text(
                                                                            status,
                                                                            modifier = Modifier
                                                                                .fillMaxWidth()
                                                                                .padding(20.dp),
                                                                            fontFamily = fontFamily,
                                                                            fontSize = 16.sp
                                                                        )
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    },
                                                    confirmButton = {
                                                        Button(onClick = {
                                                            showStatusDialog = false
                                                        }, modifier = Modifier.fillMaxWidth(),
                                                            colors = ButtonDefaults.buttonColors(
                                                                containerColor = CustomColors.primaryBlack,
                                                            )
                                                        ) {
                                                            Text(
                                                                "Close",
                                                                fontFamily = fontFamily,
                                                                color = Color.White,
                                                                fontWeight = FontWeight.Normal
                                                            )
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
    }
}


@Composable
fun OrderFilterChips(
    totalOrders: Int = 0,
    selectedFilters: Set<String> = emptySet(),
    onFilterSelected: (String) -> Unit,
    onRefreshClick: () -> Unit,
    refreshRotation: Float = 0f,
    pendingCount : Int = 0,
    inTransitCount : Int = 0,
    deliveredCount : Int = 0,
    cancelledCount : Int = 0
) {
    val statusOptions = listOf("Pending", "In Transit", "Delivered", "Cancelled")
    var isRefreshing by remember { mutableStateOf(false) }

    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshing) 0f else 360f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "refreshRotation"
    )

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(100)
            isRefreshing = false
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(95.dp)
                .height(50.dp)
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    "$totalOrders",
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(primaryBlack)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Orders",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.CenterStart),
                color = primaryBlack
            )
        }

        IconButton(onClick = {
            isRefreshing = true
            onRefreshClick()
        }) {
            Icon(
                painter = painterResource(R.drawable.arrows_rotate_solid),
                contentDescription = "Refresh",
                modifier = Modifier
                    .rotate(refreshRotation)
                    .size(18.dp),
                tint = primaryBlack
            )
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(statusOptions) { status ->
            val isSelected = selectedFilters.contains(status)
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) primaryBlack else Color.Transparent,
                animationSpec = tween(durationMillis = 300),
                label = "chipBackgroundColor"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.DarkGray,
                animationSpec = tween(durationMillis = 300),
                label = "chipContentColor"
            )

            AnimatedVisibility(
                visible = true,
                enter = slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(durationMillis = 500, delayMillis = statusOptions.indexOf(status) * 100)
                ) + fadeIn(
                    animationSpec = tween(durationMillis = 300, delayMillis = statusOptions.indexOf(status) * 100)
                )
            ) {
                Box(Modifier.clip(RoundedCornerShape(10.dp))) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) backgroundColor else Color.Transparent,
                        modifier = Modifier
                            .height(36.dp)
                            .border(
                                border = BorderStroke(1.dp, primaryBlack),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onFilterSelected(status) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally(
                                    initialOffsetX = { -it },
                                    animationSpec = tween(durationMillis = 500, delayMillis = statusOptions.indexOf(status) * 100)
                                )
                            ) {
                                if(!isSelected){
                                    Icon(
                                        painter = painterResource(
                                            when (status) {
                                                "Delivered" -> R.drawable.truck_solid
                                                "In Transit" -> R.drawable.transit
                                                "Cancelled" -> R.drawable.xmark_solid
                                                else -> R.drawable.pending
                                            }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = contentColor
                                    )
                                }else{
                                    Box(modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)) {
                                        Box(modifier = Modifier
                                            .fillMaxSize()
                                            .background(contentColor)
                                            .align(Alignment.Center)){
                                            Text(
                                                text = when (status) {
                                                    "Delivered" -> deliveredCount.toString()
                                                    "In Transit" -> inTransitCount.toString()
                                                    "Cancelled" -> cancelledCount.toString()
                                                    else -> pendingCount.toString()
                                                },
                                                color = backgroundColor,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth(),
                                                fontFamily = fontFamily,
                                                fontWeight = FontWeight.Normal,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = status,
                                color = if (isSelected) Color.White else Color.DarkGray,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
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
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = primaryBlack,
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
                painter = painterResource(R.drawable.change),
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}