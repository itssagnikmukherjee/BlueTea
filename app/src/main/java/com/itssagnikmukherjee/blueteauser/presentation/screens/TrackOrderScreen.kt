package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Colors
import androidx.compose.material.IconButton
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AppShortcut
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.binayshaw7777.kotstep.model.LineDefault
import com.binayshaw7777.kotstep.model.LineType
import com.binayshaw7777.kotstep.model.StepDefaults
import com.binayshaw7777.kotstep.model.StepStyle
import com.binayshaw7777.kotstep.model.iconVerticalWithLabel
import com.binayshaw7777.kotstep.model.tabVerticalWithLabel
import com.binayshaw7777.kotstep.ui.vertical.VerticalStepper
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.HeadingTextWithBadge
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import kotlinx.coroutines.launch
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
    val getProductsState = viewModel.getProductState.collectAsState()

    val orders =
        getUserDetailsState.value.data?.orderedItems as? Map<String, Map<String, Any>> ?: emptyMap()
    val products = getProductsState.value.data ?: emptyList()
    val productMap = remember(products) { products.associateBy { it.productId } }

    val orderDetails = orders[orderId] ?: emptyMap()
    val status = orderDetails["status"] as? String ?: "Pending"
    val placedTime = orderDetails["timestamp"] as? Long ?: 0L
    val transitTime = orderDetails["transitTime"] as? Long ?: 0L
    val deliveredTime = orderDetails["deliveredTime"] as? Long ?: 0L

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
        viewModel.getProducts()
    }

    if(getUserDetailsState.value.isLoading){
        ShimmerScreen()
    }else{
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .fillMaxWidth(0.9f).verticalScroll(rememberScrollState()),
        ) {
            val items = orderDetails["items"] as? Map<String, Long> ?: emptyMap()
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
                    text = "Track Order",
                    badgeText = "",
                    width = 120,
                    isBadgeVisible = false
                )
                CustomIconButton(
                    onClick = { viewModel.getUserDetails(userId) },
                    icon = R.drawable.reload,
                    contentDescription = "reload"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "# $orderId",
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                fontFamily = fontFamily,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            if (orderDetails.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    items.forEach { (productId, quantity) ->
                        val product = productMap[productId]
                        if (product != null) {
                            Column {
                                Row(
                                    modifier = Modifier.padding(top = 20.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.Top,
                                ) {
                                    AsyncImage(
                                        model = product.productImages[0],
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                    )

                                    Spacer(Modifier.width(4.dp))

                                    Column(
                                        modifier = Modifier
                                            .padding(10.dp),
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
                                            ) {
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
                                    }
                                }
                            }
                        } else {
                            Text(text = "- Unknown Product ($productId) x $quantity")
                        }
                    }
                }
            } else {
                Text(
                    text = "Order details not found",
                    fontFamily = fontFamily,
                    color = primaryBlack,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            //total price
            Spacer(Modifier.height(10.dp))
            val totalPrice = orderDetails["totalPrice"] as? Double ?: 0.0
            Column {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "${if(orderDetails["status"]=="Pending") "Order placed" else orderDetails["status"]}",
                        fontFamily = fontFamily,
                        color = primaryBlack,
                        fontSize = 16.sp)
                    val totalPriceWithDelivery = totalPrice + 40 as Int
                    val time = when(orderDetails["status"]){
                        "Pending" -> formatTimestamp(placedTime)
                        "In Transit" -> formatTimestamp(transitTime)
                        "Delivered" -> formatTimestamp(deliveredTime)
                        else -> 0L
                    }
                    Text(text = time.toString(), fontFamily = fontFamily, color = primaryBlack, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                }

                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = "${formatPrice(totalPrice)} + ₹40",
                        fontFamily = fontFamily,
                        color = primaryBlack,
                        fontSize = 20.sp)
                    val totalPriceWithDelivery = totalPrice + 40 as Int
                    Text("${formatPrice(totalPriceWithDelivery)}", fontFamily = fontFamily, color = primaryBlack, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Order Details", fontFamily = fontFamily, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Text(status, fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.Light)
            }
            Spacer(modifier = Modifier.height(5.dp))

            StepIndicator(status, placedTime, transitTime, deliveredTime)

            Spacer(modifier = Modifier.height(10.dp))

            val userDetails = getUserDetailsState.value.data
            userDetails?.let {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    var isShippingDetailsExpanded by remember { mutableStateOf(true) }
                    val rotation by animateFloatAsState(
                        targetValue = if (isShippingDetailsExpanded) 180f else 0f,
                        animationSpec = spring(stiffness = Spring.StiffnessMedium)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Shipping Details",
                            fontFamily = fontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(onClick = {
                            isShippingDetailsExpanded = !isShippingDetailsExpanded
                        }, modifier = Modifier.rotate(rotation)) {
                            Icon(
                                painter = painterResource(R.drawable.up),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    val randomPin = (700000..750000).random()
                    Spacer(Modifier.height(6.dp))
                    AnimatedVisibility(visible = isShippingDetailsExpanded) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${it.firstName} ${it.lastName}",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                Text(
                                    text = "${it.address}",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "$randomPin",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 12.sp
                                )
                            }
                            Column(
                                horizontalAlignment = Alignment.End,
                            ) {
                                Text(
                                    text = "${it.phoneNo}",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )

                                Text(
                                    text = "${it.email}",
                                    fontFamily = fontFamily,
                                    color = primaryBlack,
                                    fontSize = 12.sp
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

@Composable
fun StepIndicator(status: String, placedTime: Long, transitTime: Long, deliveredTime: Long) {
    Spacer(Modifier.height(20.dp))
    val steps: List<Pair<String, Long>> = listOf(
        "Order Placed" to placedTime,
        "In Transit" to (transitTime.takeIf { status == "In Transit" || status == "Delivered" } ?: 0L),
        "Delivered" to (deliveredTime.takeIf { status == "Delivered" } ?: 0L)
    )

    val currentStep = when(status){
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
        lineSize = 30.dp,
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

    Column(modifier = Modifier.padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        VerticalStepper(
            style = iconVerticalWithLabel(
                stepStyle = customStepStyle,
                currentStep = currentStep,
                icons = listOf(
                    Icons.Default.AppShortcut,
                    Icons.Default.AccessTimeFilled,
                    Icons.Default.LocalShipping
                ),
                trailingLabels = steps.map { (title, time) ->
                    {
                        Column(
                            modifier = Modifier.fillMaxHeight().padding(vertical = 10.dp),
                            verticalArrangement = Arrangement.Center
                        ){
                            Text(text = title, fontFamily = fontFamily, fontSize = 16.sp, color = if(time > 0L) primaryBlack else CustomColors.mediumGray)
                            if (time > 0L) {
                                Text(formatTimestamp(time), fontSize = 12.sp, color = CustomColors.darkGray, fontFamily = fontFamily)
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

fun formatPrice(price: Double): String {
    return if (price % 1 == 0.0) {
        "₹${price.toInt()}"
    } else {
        "₹$price"
    }
}