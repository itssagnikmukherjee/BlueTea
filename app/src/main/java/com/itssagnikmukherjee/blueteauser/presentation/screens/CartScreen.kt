package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    userId: String
) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val getProductsState = viewModel.getProductState.collectAsState()

    val productID = getUserDetailsState.value.data?.cartItems ?: emptyMap()

    val cartProducts = getProductsState.value.data ?: emptyList()

    val cartItems = remember(cartProducts, productID) {
        cartProducts.filter { it.productId in productID.keys }
    }

    LaunchedEffect(userId) {
        viewModel.getUserDetails(userId)
        viewModel.getProducts()
    }

    Column {
        LazyColumn {
            items(cartItems.size, key = { cartItems[it].productId }) { cartProduct ->
                CartItem(
                    product = cartProducts[cartProduct],
                    initialQuantity = productID[cartProducts[cartProduct].productId] ?: 0,
                    onQuantityUpdate = { newQuantity ->
                        viewModel.updateCartQuantity(
                            userId,
                            cartProducts[cartProduct].productId,
                            newQuantity
                        )
                    },
                    navController = navController,
                    userId = userId
                )
            }
        }

        Text("Total Items: ${cartItems.size}")
        Text("Total Price: ${cartItems.sumOf { it.productFinalPrice.toDouble() }}")
        Button(onClick = { /* Handle checkout action */ }) {
            Text("Checkout (${cartItems.size})")
        }
    }
}


@Composable
fun CartItem(
    product: Product,
    initialQuantity: Int,
    onQuantityUpdate: (Int) -> Unit,
    navController: NavController,
    userId: String
) {
    var quantity by rememberSaveable { mutableIntStateOf(initialQuantity) }

    val quantityText by remember { derivedStateOf { "Quantity: $quantity" } }

    Row {
        // Product Image
        AsyncImage(
            model = product.productImages[0],
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        // Product Details
        Column {
            Text(product.productName)
            Text(product.productDescription)
            Text("Pre-price: ${product.productPrePrice}")
            Text("Final price: ${product.productFinalPrice}")

            // Quantity Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        onQuantityUpdate(quantity)
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Decrease Quantity")
                }
                Text(quantityText) // Only this Text will recompose when quantity changes
                IconButton(onClick = {
                    quantity++
                    onQuantityUpdate(quantity)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                }
            }

            // Buy Now Button
            Button(onClick = {
                navController.navigate(Routes.BuyNowScreen(products = listOf(product.productId), totalPrice = product.productFinalPrice.toDouble(), userId = userId))
            }) {
                Text("Buy Now")
            }
        }
    }
}