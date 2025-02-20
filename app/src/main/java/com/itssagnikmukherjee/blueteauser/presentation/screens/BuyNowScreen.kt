package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.BuildConfig
import com.itssagnikmukherjee.blueteauser.presentation.PaymentViewModel
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.stripe.android.PaymentConfiguration
import com.stripe.android.PaymentIntentResult
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethod
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentResult
import com.stripe.android.payments.paymentlauncher.StripePaymentLauncher
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.json.JSONObject

@Composable
fun BuyNowScreen(
    navController: NavController,
    viewModel: ViewModels = hiltViewModel(),
    paymentViewModel : PaymentViewModel = hiltViewModel(),
    cartItems: List<String>,
    userId: String,
    quantity: String,
    paymentSheet: PaymentSheet
) {

    var shippingAddress by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }

    val getProductsState = viewModel.getProductState.collectAsState()
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()

    val userData = getUserDetailsState.value.data
    var phoneNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(userData) {
        userData?.address?.let { shippingAddress = it }
        userData?.phoneNo?.let { phoneNo = it.toString() }
        userData?.email?.let { email = it.toString() }
    }

    LaunchedEffect(userId) {
        paymentViewModel.fetchEphemeralKey(userId)
    }


    fun presentPaymentSheet() {
        val clientSecret = "pi_3QuVQwSCfayi15o209uDvjTQ_secret_HSorm2BbAXIia8t78jYOTMP1z"

        val config = PaymentSheet.Configuration("Demo Merchant")
        paymentSheet.presentWithPaymentIntent(clientSecret, config)
    }


    val allProducts = getProductsState.value.data ?: emptyList()
    val products = remember(cartItems, allProducts) {
        allProducts.filter { it.productId in cartItems }
    }

    var quantityMap by remember { mutableStateOf(Json.decodeFromString<Map<String, Int>>(quantity)) }

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
            val productId = product.productId
            val currentQuantity = quantityMap[productId] ?: 1

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (currentQuantity > 1) {
                                    quantityMap = quantityMap.toMutableMap().apply {
                                        this[productId] = currentQuantity - 1
                                    }
                                    viewModel.updateCartList(userId, productId, currentQuantity - 1, true)
                                }
                            }
                        ) {
                            Text("-")
                        }

                        Text("Quantity: $currentQuantity")

                        IconButton(
                            onClick = {
                                quantityMap = quantityMap.toMutableMap().apply {
                                    this[productId] = currentQuantity + 1
                                }
                                viewModel.updateCartList(userId, productId, currentQuantity + 1, true)
                            }
                        ) {
                            Text("+")
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val totalPrice = products.sumOf { product ->
            val quantity = quantityMap[product.productId] ?: 1
            product.productFinalPrice.toDouble() * quantity
        }

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
            value = phoneNo,
            onValueChange = { phoneNo = it },
            label = { Text("Enter your phone number") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Payment Method")

        val paymentOptions = listOf("Cash on Delivery", "Credit Card", "UPI", "Net Banking")

        Column {
            paymentOptions.forEach { method ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPaymentMethod = method }
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = (selectedPaymentMethod == method),
                            onClick = { selectedPaymentMethod = method }
                        )
                        Text(
                            text = method,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (selectedPaymentMethod == "UPI" && method == "UPI") {
                        OutlinedTextField(
                            value = "upiId@oksbi",
                            onValueChange = { },
                            label = { Text("Enter UPI ID") },
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        )
                    }
                    if (selectedPaymentMethod == "Credit Card" && method == "Credit Card" ||
                        selectedPaymentMethod == "Net Banking" && method == "Net Banking"
                    ) {
                        Button(
                            onClick = {
                                presentPaymentSheet()
                            }) {
                            Text("Pay via Stripe")
                        }
                    }
                }
            }
        }


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

