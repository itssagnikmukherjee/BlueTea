package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import kotlinx.serialization.json.Json

@Composable
fun WishListScreen(navController: NavController, viewModel: ViewModels = hiltViewModel(), userId: String) {
    Scaffold{innerPadding->
        val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
        val getProductsState = viewModel.getProductState.collectAsState()

        val productID = getUserDetailsState.value.data?.wishlistItems ?: emptyList()
        val products = getProductsState.value.data ?: emptyList()

        val wishlistProducts = products.filter { product -> product.productId in productID }

        LaunchedEffect(Unit) {
            viewModel.getUserDetails(userId)
            viewModel.getProducts()
        }

        Log.d("WishListScreen", "Firestore Wishlist: $productID")
        Log.d("WishListScreen", "Filtered Wishlist Products: $wishlistProducts")
        if(getUserDetailsState.value.isLoading || getProductsState.value.isLoading){ShimmerScreen()} else
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(wishlistProducts.size == 0){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                Text("No Wishlist Items", fontFamily = fontFamily)
                }
            }else
            LazyColumn {
                items(wishlistProducts.size) { index ->
                    WishListItem(
                        product = wishlistProducts[index],
                        userId = userId,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun WishListItem(
    product: Product,
    viewModel: ViewModels = hiltViewModel(),
    userId: String,
    navController: NavController
) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    Card(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp)).height(180.dp),
    ){
        Box (
            modifier = Modifier.fillMaxSize()
        ){
            IconButton(
                onClick = {
                    viewModel.updateFavoriteList(
                        userId = userId,
                        productId = product.productId,
                        isFavorite = false
                    )
                },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }
            Row {
                AsyncImage(
                    model = product.productImages[0], contentDescription = "",
                    modifier = Modifier.size(180.dp)
                )
                Column (
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp).fillMaxHeight(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Column {
                    Text(text = product.productName , fontFamily= fontFamily, color = primaryBlack, fontSize = 18.sp)
                    Row {
                        Text(text = "₹${product.productPrePrice}" , fontFamily= fontFamily, color = primaryBlack, textDecoration = TextDecoration.LineThrough)
                        Spacer(Modifier.width(10.dp))
                        Text(text = "₹${product.productFinalPrice}" , fontFamily= fontFamily, color = primaryBlack, fontSize = 23.sp)
                    }
                    }
                    Column {
                        Button(onClick = {
                            navController.navigate(
                                Routes.BuyNowScreen(
                                    products = listOf(product.productId),
                                    totalPrice = product.productFinalPrice.toDouble(),
                                    userId = userId,
                                    quantity = Json.encodeToString(mapOf(product.productId to 1))
                                )
                            )
                        },
                            modifier = Modifier.height(40.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryBlack
                            )
                            ) {
                            Text("Buy Now", fontFamily = fontFamily)
                        }
                        Spacer(Modifier.height(5.dp))
                        Button(onClick = {
                            viewModel.updateCartList(
                                userId = userId,
                                productId = product.productId,
                                quantity = 1,
                                isCarted = true
                            )
                        },
                            modifier = Modifier.height(40.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                            ) {
                            val cartItems = getUserDetailsState.value.data?.cartItems ?: emptyMap()
                            if (product.productId in cartItems.keys) {
                                Text("In Cart", fontFamily = fontFamily)
                            } else
                                Text("Add to Cart", fontFamily = fontFamily)
                        }
                    }
                }
            }
        }
    }
}
