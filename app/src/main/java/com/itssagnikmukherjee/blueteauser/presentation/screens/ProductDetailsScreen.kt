package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductDetailsScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController, productId: String, userId: String) {
    val context = LocalContext.current
    val getProductDetailsState = viewModel.getProductDetailsState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val productID = getUserDetailsState.value.data?.cartItems ?: emptyMap()
    var quantity = productID[productId] ?: 1
    LaunchedEffect(Unit) {
        if (productId.isNotEmpty() && userId.isNotEmpty()) {
            viewModel.getProductDetails(productId)
            viewModel.getUserDetails(userId)
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show()
        }
    }

    var isFavorite by remember { mutableStateOf(false) }
    isFavorite = getUserDetailsState.value.data?.wishlistItems?.contains(productId) == true


    Log.d("ProductDetailsScreen", "Firestore Wishlist: ${getUserDetailsState.value.data?.wishlistItems}")
    Log.d("ProductDetailsScreen", "isFavorite: $isFavorite")

    when {
        getProductDetailsState.value.isLoading -> {
            ShimmerScreen()
        }

        getProductDetailsState.value.error != null -> {
            Toast.makeText(context, getProductDetailsState.value.error, Toast.LENGTH_SHORT).show()
        }

        getProductDetailsState.value.data != null -> {
            val productImages = getProductDetailsState.value.data!!.productImages
            val pagerState = rememberPagerState(pageCount = { productImages.size })

            Scaffold { innerPadding ->
                Column(
                    Modifier.fillMaxSize().padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxHeight().fillMaxWidth(.9f)
                    ) {
                        Row(
                            modifier = Modifier.zIndex(999f).fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomIconButton(
                                onClick = { navController.popBackStack() },
                                icon = R.drawable.back, contentDescription = "back"
                            )
                            CustomIconButton(
                                onClick = {
                                    isFavorite = !isFavorite
                                    viewModel.updateFavoriteList(
                                        userId = userId,
                                        productId = productId,
                                        isFavorite = isFavorite
                                    )
                                },
                                icon = if (!isFavorite) R.drawable.heart_outlined else R.drawable.heart_filled,
                                contentDescription = "fav",
                                color = CustomColors.primaryBlack
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                Box (Modifier.clip(RoundedCornerShape(20.dp))){
                                    AsyncImage(
                                        model = productImages[page],
                                        contentDescription = "",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth().size(370.dp)
                                            .clip(RoundedCornerShape(20.dp))
                                            .pointerInput(Unit) {
                                                detectHorizontalDragGestures { _, dragAmount ->
                                                    when {
                                                        dragAmount > 50 -> coroutineScope.launch {
                                                            pagerState.animateScrollToPage(
                                                                (pagerState.currentPage - 1).coerceAtLeast(
                                                                    0
                                                                )
                                                            )
                                                        }

                                                        dragAmount < -50 -> coroutineScope.launch {
                                                            pagerState.animateScrollToPage(
                                                                (pagerState.currentPage + 1).coerceAtMost(
                                                                    productImages.size - 1
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                    )


                                    Box(Modifier.padding(10.dp).clip(CircleShape).background(CustomColors.primaryBlack).size(48.dp).zIndex(999f).align(Alignment.BottomStart),
                                        contentAlignment = Alignment.Center
                                    ){
                                        val prePrice = getProductDetailsState.value.data!!.productPrePrice.toFloat()
                                        val finalPrice = getProductDetailsState.value.data!!.productFinalPrice.toFloat()
                                        val discountPercentage = ((prePrice - finalPrice) / prePrice * 100).toInt()
                                        Text(
                                            text = "$discountPercentage%",
                                            fontFamily = fontFamily,
                                            color = Color.White,
                                            fontSize = 16.sp,
                                        )
                                    }

                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ){
                                items(productImages.size) { index ->
                                    val isSelected = pagerState.currentPage == index

                                    val borderWidth by animateDpAsState(
                                        targetValue = if (isSelected) 4.dp else 0.dp,
                                        animationSpec = tween(durationMillis = 200)
                                    )

                                    val borderColor by animateColorAsState(
                                        targetValue = if (isSelected) CustomColors.mediumGray else Color.Transparent,
                                        animationSpec = tween(durationMillis = 200)
                                    )

                                    AsyncImage(
                                        model = productImages[index],
                                        contentDescription = "",
                                        modifier = Modifier
                                            .size(100.dp).clip(RoundedCornerShape(20.dp))
                                            .border(
                                                width = borderWidth,
                                                color = borderColor,
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .clickable {
                                                coroutineScope.launch {
                                                    pagerState.animateScrollToPage(
                                                        index
                                                    )
                                                }
                                            }
                                    )
                                }
                            }

                            Spacer(Modifier.height(60.dp))

                            val productDesc = getProductDetailsState.value.data!!.productDescription
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column {

                                        Text(
                                            text = getProductDetailsState.value.data!!.productName,
                                            fontSize = 26.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(Modifier.height(6.dp))
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.star_rating),
                                                "",
                                                tint = Color(0xFFFFAB62),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Text(
                                                text = "4.5",
                                                fontFamily = fontFamily,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Light,
                                                color = CustomColors.primaryBlack
                                            )
                                            Text(
                                                "(2132 reviews)",
                                                fontFamily = fontFamily,
                                                fontSize = 16.sp,
                                                color = CustomColors.primaryBlack.copy(0.9f)
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        IconButton(
                                            onClick = {
                                                viewModel.updateCartQuantity(
                                                    userId = userId,
                                                    productId = productId,
                                                    quantity = quantity - 1
                                                )
                                                quantity--
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color(0xFFD9D9D9)
                                            ),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Text("-", fontFamily = fontFamily)
                                        }
                                        Text(
                                            text = quantity.toString(),
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp,
                                            color = CustomColors.primaryBlack
                                        )
                                        IconButton(
                                            onClick = {
                                                viewModel.updateCartQuantity(
                                                    userId = userId,
                                                    productId = productId,
                                                    quantity = quantity + 1
                                                )
                                                quantity++
                                            },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = Color(0xFFD9D9D9)
                                            ),
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Text("+", fontFamily = fontFamily, fontSize = 16.sp)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier.fillMaxWidth(0.75f),
                                ) {
                                    Text(
                                        text = if (productDesc.length > 50) productDesc.substring(
                                            0,
                                            82
                                        ) else productDesc,
                                        fontFamily = fontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Light
                                    )
                                    if (productDesc.length > 50)
                                        Text(
                                            "Read More ...",
                                            fontFamily = fontFamily,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            color = CustomColors.primaryBlack,
                                            modifier = Modifier.align(Alignment.BottomEnd)
                                        )
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "₹${getProductDetailsState.value.data!!.productFinalPrice}",
                                    fontFamily = fontFamily,
                                    color = CustomColors.primaryBlack,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "₹${getProductDetailsState.value.data!!.productPrePrice}",
                                    fontFamily = fontFamily,
                                    color = CustomColors.primaryBlack,
                                    fontWeight = FontWeight.Light,
                                    textDecoration = TextDecoration.LineThrough
                                )

                            }

                            Spacer(Modifier.height(20.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                IconButton(onClick = {
                                    navController.navigate(Routes.CartScreen(userId))
                                }, modifier = Modifier.border(2.dp, CustomColors.primaryBlack, CircleShape).padding(10.dp)) {
                                    Icon(painter = painterResource(R.drawable.cart_filled),"", modifier = Modifier.size(24.dp))
                                }

                                Button(onClick = {
                                    val quantityMap =
                                        Json.encodeToString(mapOf(productId to quantity))
                                    navController.navigate(Routes.BuyNowScreen(
                                        products = listOf(productId.toString()),
                                        userId = userId,
                                        totalPrice = getProductDetailsState.value.data!!.productFinalPrice.toDouble() * quantity,
                                        quantity = quantityMap
                                    ))

                                }, colors = ButtonDefaults.buttonColors(
                                    containerColor = CustomColors.primaryBlack,
                                    contentColor = Color.White
                                )){
                                    Text("Buy Now", fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.White, modifier = Modifier.padding(horizontal = 80.dp, vertical = 14.dp))
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
    }
}
