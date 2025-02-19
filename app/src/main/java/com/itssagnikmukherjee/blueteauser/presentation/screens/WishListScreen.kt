package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import kotlinx.serialization.json.Json

@Composable
fun WishListScreen(navController: NavController, viewModel: ViewModels = hiltViewModel(), userId: String) {
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

    LazyColumn {
        items(wishlistProducts.size) { index ->
            WishListItem(product = wishlistProducts[index], userId = userId, navController = navController)
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
    Box {
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
                modifier = Modifier.size(200.dp)
            )
            Column {
                Text(text = product.productName)
                Text(text = product.productDescription)
                Text(text = product.productPrePrice.toString())
                Text(text = product.productFinalPrice.toString())

                Button(onClick = {
                    navController.navigate(
                        Routes.BuyNowScreen(
                            products = listOf(product.productId),
                            totalPrice = product.productFinalPrice.toDouble(),
                            userId = userId,
                            quantity = Json.encodeToString(mapOf(product.productId to 1))
                        )
                    )
                }) {
                    Text("Buy Now")
                }

                Button(onClick = {
                    viewModel.updateCartList(userId = userId, productId = product.productId, quantity = 1, isCarted = true)
                }) {
                    //if added to cart then show "Added to Cart"
                    val cartItems = getUserDetailsState.value.data?.cartItems ?: emptyMap()
                    if (product.productId in cartItems.keys) {
                        Text("Added to Cart")
                    } else
                    Text("Add to Cart")
                }
            }
        }
    }
}
