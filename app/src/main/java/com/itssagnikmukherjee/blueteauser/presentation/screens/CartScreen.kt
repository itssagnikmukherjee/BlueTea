package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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

    Scaffold{ paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartContent(Modifier.padding(paddingValues))
        } else {
            Column(modifier = Modifier.padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(.9f).padding(top = 10.dp),
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
                        badgeText = cartItems.size.toString(),
                        width = 66
                    )
                    CustomIconButton(
                        onClick = { viewModel.getUserDetails(userId) },
                        icon = R.drawable.reload,
                        contentDescription = "back"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(.9f).padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ){
                    Column{
                        Text("Cart Total", fontSize = 20.sp, fontFamily = fontFamily, color = primaryBlack, fontWeight = FontWeight.Medium)
                        Text("₹$totalPrice", fontSize = 26.sp, fontFamily = fontFamily, color = primaryBlack, fontWeight = FontWeight.SemiBold)
                    }
                    Row {
                        CustomButton1(onclick = {
                            navController.navigate(Routes.OrdersScreen(userId))
                        }, text = "Orders")
                        Spacer(Modifier.width(10.dp))
                    CustomButtonFilled(onclick = {
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
                    }, text = "Checkout")
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp)
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
                                val quantityMap =
                                    Json.encodeToString(mapOf(product.productId to quantity))
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp).clickable{
                navController.navigate(Routes.ProductDetailsScreen(product.productId, userId))
            }
    ) {
        Row(
            modifier = Modifier.padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.productImages[0],
                contentDescription = product.productName,
                modifier = Modifier
                    .size(150.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = fontFamily,
                    color = primaryBlack,
                    fontSize = 16.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.productFinalPrice}",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${product.productPrePrice}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (quantity > 1) {
                                quantity--
                                onQuantityUpdate(quantity)
                            }
                        },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Decrease Quantity",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            quantity++
                            onQuantityUpdate(quantity)
                        },
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                            .size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onBuyNow(quantity) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Buy Now")
                    }

                    IconButton(
                        onClick = onDeleteItem,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                shape = CircleShape
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Item",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
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
            Icon(
                Icons.Default.ShoppingCart,
                contentDescription = "Empty Cart",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your cart is empty",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Looks like you haven't added any items to your cart yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {  }) {
                Text("Start Shopping")
            }
        }
    }
}
