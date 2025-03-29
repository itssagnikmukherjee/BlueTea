package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.RadioButtonColors
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.BuildConfig
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.PaymentViewModel
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomButtonFilled
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.HeadingTextWithBadge
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteauser.presentation.theme.headingTextStyle
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

@RequiresApi(Build.VERSION_CODES.O)
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

    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }

    val getProductsState = viewModel.getProductState.collectAsState()
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()

    val userData = getUserDetailsState.value.data
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    val randomPincode = (700000..750000).random()
    var phoneNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current
    LaunchedEffect(userData) {
        userData?.firstName?.let{ firstName = it }
        userData?.lastName?.let { lastName = it }
        userData?.address?.let { address = it }
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
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding).padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column{
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(90.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomIconButton(
                        onClick = { navController.popBackStack() },
                        icon = R.drawable.back,
                        contentDescription = "back"
                    )
                    HeadingTextWithBadge(
                        text = "Checkout",
                        badgeText = cartItems.size.toString(),
                        width = 120
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                products.forEach { product ->
                    val productId = product.productId
                    val currentQuantity = quantityMap[productId] ?: 1
                    ItemCard(
                        product = product,
                        initialQuantity = currentQuantity,
                        onQuantityUpdate = { newQuantity ->
                            quantityMap = quantityMap.toMutableMap().apply {
                                this[productId] = newQuantity
                            }
                        },
                        navController = navController,
                        userId = userId
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                val totalPrice = products.sumOf { product ->
                    val quantity = quantityMap[product.productId] ?: 1
                    product.productFinalPrice.toDouble() * quantity
                }

                Column {
                    var isShippingExpanded by remember { mutableStateOf(true) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Shipping Details", style = headingTextStyle, fontSize = 20.sp)
                        IconButton(onClick = {
                            isShippingExpanded = !isShippingExpanded
                        }) {
                            Icon(
                                painter = painterResource(id = if (isShippingExpanded) R.drawable.down else R.drawable.up),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = isShippingExpanded
                    ) {
                            Column(
                                modifier = Modifier.padding(vertical = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ){
                                val customTextFieldStyle = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CustomColors.mediumGray,
                                    unfocusedBorderColor = CustomColors.mediumGray,
                                    disabledBorderColor = Color.Transparent,
                                    focusedTextColor = CustomColors.primaryBlack,
                                    unfocusedTextColor = CustomColors.primaryBlack,
                                    disabledTextColor = CustomColors.primaryBlack,
                                    cursorColor = CustomColors.primaryBlack,
                                    focusedLabelColor = CustomColors.darkGray,
                                    unfocusedLabelColor = CustomColors.darkGray
                                )
                                Row{
                                    OutlinedTextField(
                                        value = firstName,
                                        onValueChange = { firstName = it },
                                        label = { Text("First Name", fontFamily = fontFamily) },
                                        modifier = Modifier.weight(1f),
                                        colors = customTextFieldStyle,
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    OutlinedTextField(
                                        value = lastName,
                                        onValueChange = { lastName = it },
                                        label = { Text("Last name") },
                                        modifier = Modifier.weight(1f),
                                        colors = customTextFieldStyle,
                                        shape = RoundedCornerShape(15.dp),
                                        )

                                }
                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Address") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = customTextFieldStyle,
                                    shape = RoundedCornerShape(15.dp)
                                )

                                OutlinedTextField(
                                    value = email,
                                    onValueChange = { email = it },
                                    label = { Text("Enter your email") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = customTextFieldStyle,
                                    shape = RoundedCornerShape(15.dp)
                                )

                                OutlinedTextField(
                                    value = phoneNo,
                                    onValueChange = { phoneNo = it },
                                    label = { Text("Enter your phone number") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = customTextFieldStyle,
                                    shape = RoundedCornerShape(15.dp)
                                )
                            }
                    }

                }


                Row(
                    Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text("Summary" , style = headingTextStyle, fontSize = 20.sp)
                }
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        val tot = if (products.size>1) "Total (${products.size} items)" else "Total (1 item)"
                        Text(tot, fontSize = 16.sp, fontFamily = fontFamily)
                        Text("₹$totalPrice",fontFamily= fontFamily, fontSize = 26.sp, fontWeight = FontWeight.SemiBold, color = CustomColors.primaryBlack)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Shipping Charges", fontSize = 16.sp, fontFamily = fontFamily, color = CustomColors.primaryBlack)
                        Text("₹40.0", fontSize = 26.sp,fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = CustomColors.primaryBlack)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text("Subtotal", fontSize = 16.sp, fontFamily = fontFamily, color = CustomColors.primaryBlack)
                        Text("₹${totalPrice+40.0}", fontSize = 26.sp,fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = CustomColors.primaryBlack)
                    }

                }

                Spacer(Modifier.height(20.dp))

                Text("Payment Method", style = headingTextStyle, fontSize = 20.sp)

                val paymentOptions = listOf("Cash on Delivery", "Credit Card", "UPI", "Net Banking")

                Column(
                    modifier = Modifier.padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ){
                    paymentOptions.forEach { method ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedPaymentMethod = method }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = (selectedPaymentMethod == method),
                                    onClick = { selectedPaymentMethod = method },
                                    colors = androidx.compose.material3.RadioButtonDefaults.colors(
                                        selectedColor = CustomColors.primaryBlack,
                                        unselectedColor = CustomColors.darkGray
                                    ),
                                )
                                Text(
                                    text = method,
                                    fontFamily = fontFamily,
                                    fontSize = 16.sp,
                                    color = CustomColors.primaryBlack,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            if (selectedPaymentMethod == "UPI" && method == "UPI") {
                                val customTextFieldStyle = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CustomColors.mediumGray,
                                    unfocusedBorderColor = CustomColors.mediumGray,
                                    disabledBorderColor = Color.Transparent,
                                    focusedTextColor = CustomColors.primaryBlack,
                                    unfocusedTextColor = CustomColors.primaryBlack,
                                    disabledTextColor = CustomColors.primaryBlack,
                                    cursorColor = CustomColors.primaryBlack,
                                    focusedLabelColor = CustomColors.darkGray,
                                    unfocusedLabelColor = CustomColors.darkGray
                                )
                                OutlinedTextField(
                                    value = "upiId@oksbi",
                                    onValueChange = { },
                                    label = { Text("Enter UPI ID") },
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                    colors = customTextFieldStyle,
                                    shape = RoundedCornerShape(15.dp)
                                )
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val isDirectPurchase = quantity != null
                        if(selectedPaymentMethod == "Cash on Delivery"){
                            viewModel.placeOrder(
                                userId = userId,
                                totalPrice = totalPrice,
                                address = address,
                                phone = phoneNo,
                                email = email,
                                paymentMethod = selectedPaymentMethod,
                                isDirectPurchase = isDirectPurchase,
                                directPurchaseItem = if (isDirectPurchase) Json.decodeFromString<Map<String, Int>>(
                                    quantity
                                ) else null
                            )
                            Toast.makeText(context, "Order Placed", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        else{
                            presentPaymentSheet()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = CustomColors.blackest,
                        contentColor = Color.White
                    )
                ) {
                    Text(text =
                    when(selectedPaymentMethod){
                        "Cash on Delivery" -> "Place Order"
                        "UPI" -> "UPI Pay"
                        "Net Banking" -> "Goto Net Banking"
                        else -> "Pay with Stripe"
                    }
                    , fontFamily = fontFamily, fontSize = 16.sp, fontWeight = FontWeight.Normal)
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun ItemCard(
    product: Product,
    initialQuantity: Int,
    onQuantityUpdate: (Int) -> Unit,
    navController: NavController,
    userId: String
) {
    var quantity by rememberSaveable { mutableIntStateOf(initialQuantity) }

    Card(
        modifier = Modifier.padding(vertical = 14.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ){
        Box (
            modifier = Modifier.fillMaxSize()
        ){
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
                                    onQuantityUpdate(quantity - 1)
                                    quantity--
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
                                    onQuantityUpdate(quantity + 1)
                                    quantity++
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFFD9D9D9)
                                ),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Text("+", fontFamily= fontFamily, fontSize = 16.sp)
                            }
                        }
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