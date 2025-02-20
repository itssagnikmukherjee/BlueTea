package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import kotlinx.serialization.json.Json

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

    val totalPrice = cartItems.sumOf { product ->
        val quantity = productID[product.productId] ?: 1
        product.productFinalPrice.toDouble() * quantity
    }

    Column {
        LazyColumn {
            items(cartItems.size, key = { cartItems[it].productId }) { index ->
                val product = cartItems[index]
                CartItem(
                    product = product,
                    initialQuantity = productID[product.productId] ?: 1,
                    onQuantityUpdate = { newQuantity ->
                        viewModel.updateCartQuantity(userId, product.productId, newQuantity)
                    },
                    navController = navController,
                    userId = userId
                )
            }
        }

        Text("Total Items: ${cartItems.size}")
        Text("Total Price: $$totalPrice")

        Button(onClick = {
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
        }) {
            Text("Checkout (${cartItems.size})")
        }
        Button(onClick = {}) {
            Text("Goto Orders")
        }
    }
}

@Composable
fun CartItem(
    product: Product,
    initialQuantity: Int,
    onQuantityUpdate: (Int) -> Unit,
    navController: NavController,
    userId: String,
    viewModels: ViewModels = hiltViewModel()
) {
    var quantity by rememberSaveable { mutableIntStateOf(initialQuantity) }
    Box{
        IconButton(onClick = {
            viewModels.updateCartList(userId = userId, productId = product.productId, quantity = 0, isCarted = false)
        }, modifier = Modifier.align(Alignment.TopEnd)) {
            Icon(Icons.Default.Clear, contentDescription = "Delete Item")
        }
    Row {
        AsyncImage(
            model = product.productImages[0],
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Column {
            Text(product.productName)
            Text(product.productDescription)
            Text("Pre-price: ${product.productPrePrice}")
            Text("Final price: ${product.productFinalPrice}")

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    if (quantity > 1) {
                        quantity--
                        onQuantityUpdate(quantity)
                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Decrease Quantity")
                }

                Text("Quantity: $quantity")

                IconButton(onClick = {
                    quantity++
                    onQuantityUpdate(quantity)
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Quantity")
                }
            }

            Button(onClick = {
                val quantityMap = Json.encodeToString(mapOf(product.productId to quantity))
                navController.navigate(
                    Routes.BuyNowScreen(
                        products = listOf(product.productId),
                        totalPrice = product.productFinalPrice.toDouble() * quantity,
                        userId = userId,
                        quantity = quantityMap
                    )
                )
            }) {
                Text("Buy Now")
            }
        }
    }
    }
}
