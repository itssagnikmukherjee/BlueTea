package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun BuyNowScreen(
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    cartItems: List<String>,
    totalPrice: Double,
    userId: String
) {
    var shippingAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Credit Card") }

    val getProductsState = viewModel.getProductState.collectAsState()
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()

    val userData = getUserDetailsState.value.data
    var phoneNo by remember { mutableStateOf("")}
    var email by remember { mutableStateOf("")}

    LaunchedEffect(userData) {
        userData?.address?.let {
            shippingAddress = it
        }
        userData?.phoneNo.let{
            phoneNo = it.toString()
        }
        userData?.email.let{
            email = it.toString()
        }
    }

    val allProducts = getProductsState.value.data ?: emptyList()

    val products = remember(cartItems, allProducts) {
        allProducts.filter { it.productId in cartItems }
    }

    LaunchedEffect(Unit) {
        viewModel.getProducts()
        viewModel.getUserDetails(userId)
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Order Summary")
        Spacer(modifier = Modifier.height(8.dp))

        products.forEach { product ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                AsyncImage(
                    model = product.productImages[0],
                    contentDescription = null,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(product.productName)
                    Text("Price: $${product.productFinalPrice}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Total Price: $$totalPrice")
        Spacer(modifier = Modifier.height(16.dp))

        Text("Shipping Address")
        OutlinedTextField(
            value = shippingAddress,
            onValueChange = { shippingAddress = it },
            label = { Text("Enter your shipping address") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phoneNo.toString(),
            onValueChange = { phoneNo = it },
            label = { Text("Enter your phone number") },
        )

        OutlinedTextField(
            value = email.toString(),
            onValueChange = { email = it },
            label = { Text("Enter your phone number") },
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Payment Method")
        OutlinedTextField(
            value = paymentMethod,
            onValueChange = { paymentMethod = it },
            label = { Text("Enter your payment method") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place Order")
        }
    }
}

