package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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

        if(getUserDetailsState.value.isLoading || getProductsState.value.isLoading){ShimmerScreen()} else
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Row(
                modifier = Modifier.fillMaxWidth(.9f).padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                CustomIconButton(onClick = {navController.popBackStack()}, icon = R.drawable.back, contentDescription = "back")
                HeadingTextWithBadge(text = "Favorites", badgeText = wishlistProducts.size.toString(), width = 110)
                CustomIconButton(onClick = {viewModel.getUserDetails(userId)}, icon = R.drawable.reload, contentDescription = "back")
            }

            if(wishlistProducts.size == 0){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                Text("No Wishlist Items", fontFamily = fontFamily)
                }
            }else

            Spacer(Modifier.height(30.dp))
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
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp).fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
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
                    modifier = Modifier.size(120.dp).clip(RoundedCornerShape(20.dp))
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
                    Row {
                        val cartItems = getUserDetailsState.value.data?.cartItems ?: emptyMap()
                        CustomButton1(onclick = {
                            viewModel.updateCartList(
                                userId = userId,
                                productId = product.productId,
                                quantity = 1,
                                isCarted = true
                            )
                        }, text = if (product.productId in cartItems.keys) "In Cart" else "Add to Cart")
                        Spacer(Modifier.width(5.dp))
                        CustomButtonFilled(onclick = {
                            navController.navigate(
                                Routes.BuyNowScreen(
                                    products = listOf(product.productId),
                                    totalPrice = product.productFinalPrice.toDouble(),
                                    userId = userId,
                                    quantity = Json.encodeToString(mapOf(product.productId to 1))
                                )
                            )
                        }, text = "Buy Now")
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center){
            Divider(
                thickness = 1.dp,
                color = CustomColors.mediumGray,
                modifier = Modifier.fillMaxWidth(0.7f).align(Alignment.CenterVertically)
            )
        }
    }
}
