package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.domain.models.Product
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    userId: String
) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val getProductsState = viewModel.getProductState.collectAsState()

    val isLoading = getUserDetailsState.value.isLoading || getProductsState.value.isLoading

    val productID = getUserDetailsState.value.data?.cartItems ?: emptyMap()
    val cartProducts = getProductsState.value.data ?: emptyList()

    val cartItems = remember(cartProducts, productID) {
        cartProducts.filter { it.productId in productID.keys }
    }

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
        viewModel.getProducts()
    }

    val totalPrice = cartItems.sumOf { product ->
        val quantity = productID[product.productId] ?: 1
        product.productFinalPrice.toDouble() * quantity
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HeaderSection(
                navController = navController,
                cartSize = cartItems.size,
                onRefresh = { viewModel.getUserDetails(userId) }
            )
            if (getUserDetailsState.value.isLoading || getProductsState.value.isLoading){
                    ShimmerScreen()
            }else {
                if (cartItems.isEmpty()) {
                    EmptyCartContent()
                } else {
                    CartContent(
                        cartItems = cartItems,
                        productID = productID,
                        totalPrice = totalPrice,
                        userId = userId,
                        navController = navController,
                        viewModel = viewModel,
                        paddingValues = paddingValues
                    )
                }
            }
        }
    }
}


@Composable
fun HeaderSection(
    navController: NavController,
    cartSize: Int,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(0.9f).padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomIconButton(
            onClick = { navController.popBackStack() },
            icon = R.drawable.back,
            contentDescription = "back"
        )
        HeadingTextWithBadge(
            text = "Cart",
            badgeText = cartSize.toString(),
            width = 66
        )
        CustomIconButton(
            onClick = { onRefresh() },
            icon = R.drawable.reload,
            contentDescription = "refresh"
        )
    }
}


@Composable
fun CartContent(
    navController: NavController,
    cartItems: List<Product>,
    productID: Map<String, Int>,
    totalPrice: Double,
    userId: String,
    paddingValues: PaddingValues,
    viewModel: ViewModels
) {
    Column(
        modifier = Modifier.padding(top = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(.9f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    "Cart Total", fontSize = 20.sp, fontFamily = fontFamily,
                    color = primaryBlack, fontWeight = FontWeight.Medium
                )
                Text(
                    "₹$totalPrice", fontSize = 26.sp, fontFamily = fontFamily,
                    color = primaryBlack, fontWeight = FontWeight.SemiBold
                )
            }
            Row {
                CustomButton1(
                    onclick = { navController.navigate(Routes.OrdersScreen(userId)) },
                    text = "Orders"
                )
                Spacer(Modifier.width(10.dp))
                CustomButtonFilled(
                    onclick = {
                        val cartItemsMap = cartItems.associate { it.productId to (productID[it.productId] ?: 1) }
                        val serializedQuantities = Json.encodeToString(cartItemsMap)
                        navController.navigate(
                            Routes.BuyNowScreen(
                                products = cartItems.map { it.productId },
                                totalPrice = totalPrice,
                                userId = userId,
                                quantity = serializedQuantities
                            )
                        )
                    },
                    text = "Checkout"
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(cartItems.size, key = { cartItems[it].productId }) { index ->
                val product = cartItems[index]
                CartItemCard(
                    product = product,
                    initialQuantity = productID[product.productId] ?: 1,
                    onQuantityUpdate = { newQuantity ->
                        viewModel.updateCartQuantity(userId, product.productId, newQuantity)
                    },
                    onDeleteItem = {
                        viewModel.updateCartList(
                            userId = userId,
                            productId = product.productId,
                            quantity = 0,
                            isCarted = false
                        )
                    },
                    onBuyNow = { quantity ->
                        val quantityMap = Json.encodeToString(mapOf(product.productId to quantity))
                        navController.navigate(
                            Routes.BuyNowScreen(
                                products = listOf(product.productId),
                                totalPrice = product.productFinalPrice.toDouble() * quantity,
                                userId = userId,
                                quantity = quantityMap
                            )
                        )
                    },
                    navController = navController,
                    userId = userId
                )
            }
        }
    }
}


@Composable
fun CartItemCard(
    product: Product,
    initialQuantity: Int,
    onQuantityUpdate: (Int) -> Unit,
    onDeleteItem: () -> Unit,
    onBuyNow: (Int) -> Unit,
    navController: NavController,
    userId: String
) {
    var quantity by rememberSaveable { mutableIntStateOf(initialQuantity) }

    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box (
            modifier = Modifier.fillMaxSize()
        ){
            IconButton(
                onClick = onDeleteItem,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color(0xFFD9D9D9)
                ),
                modifier = Modifier.align(Alignment.TopEnd).size(26.dp)
            ) {
                Icon(painter = painterResource(R.drawable.cross), contentDescription = "", tint = primaryBlack, modifier = Modifier.size(10.dp))
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ){
                AsyncImage(
                    model = product.productImages[0], contentDescription = "",
                    modifier = Modifier.size(120.dp).clip(RoundedCornerShape(20.dp)).clickable{
                        navController.navigate(Routes.ProductDetailsScreen(product.productId, userId))
                    }
                )
                Column (
                    modifier = Modifier.padding(10.dp).fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Column {
                        Text(text = product.productName , fontFamily= fontFamily, color = primaryBlack, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(text = product.productCategory , fontFamily= fontFamily, color = primaryBlack, fontSize = 12.sp, fontWeight = FontWeight.Light)
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ){
                            Text("₹", fontFamily= fontFamily, color = primaryBlack, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
                            Text(text = "${product.productFinalPrice}" , fontFamily= fontFamily, color = primaryBlack, fontSize = 26.sp, fontWeight = FontWeight.SemiBold)
                            Text(text = "₹${product.productPrePrice}" , fontSize = 12.sp, fontFamily= fontFamily, color = primaryBlack, textDecoration = TextDecoration.LineThrough)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ){
                            IconButton(
                                onClick = {
                                    quantity--
                                    onQuantityUpdate(quantity)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFD9D9D9)
                                ),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Text("-", fontFamily= fontFamily)
                            }
                            Text(text = quantity.toString(), fontFamily= fontFamily)
                            IconButton(
                                onClick = {
                                    quantity++
                                    onQuantityUpdate(quantity)
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFD9D9D9)
                                ),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Text("+", fontFamily= fontFamily, fontSize = 16.sp)
                            }
                        }

                        Spacer(Modifier.width(5.dp))
                        CustomButtonFilled(onclick = {onBuyNow(quantity)}, text = "Buy Now")
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
            androidx.compose.material3.Divider(
                thickness = 1.dp,
                color = CustomColors.mediumGray,
                modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun EmptyCartContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Cart is empty", fontFamily = fontFamily, fontSize = 14.sp)
        }
    }
}
