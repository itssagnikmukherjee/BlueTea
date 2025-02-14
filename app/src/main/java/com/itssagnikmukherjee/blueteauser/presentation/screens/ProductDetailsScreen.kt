package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.Alignment
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ProductDetailsScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController, productId: String) {
    val context = LocalContext.current
    val getProductDetailsState = viewModel.getProductDetailsState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (productId.isNotEmpty()) {
            viewModel.getProductDetails(productId)
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show()
        }
    }

    when {
        getProductDetailsState.value.isLoading -> {
            CircularProgressIndicator()
        }
        getProductDetailsState.value.error != null -> {
            Toast.makeText(context, getProductDetailsState.value.error, Toast.LENGTH_SHORT).show()
        }
        getProductDetailsState.value.data != null -> {
            val productImages = getProductDetailsState.value.data!!.productImages
            val pagerState = rememberPagerState(pageCount = { productImages.size })

            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.zIndex(999f)) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }

                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .zIndex(9999f)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = "")
                    }

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // **Product Image Slider**
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxWidth()
                        ) { page ->
                            AsyncImage(
                                model = productImages[page],
                                contentDescription = "",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(400.dp)
                                    .pointerInput(Unit) {
                                        detectHorizontalDragGestures { _, dragAmount ->
                                            when {
                                                dragAmount > 50 -> coroutineScope.launch {
                                                    pagerState.animateScrollToPage(
                                                        (pagerState.currentPage - 1).coerceAtLeast(0)
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
                        }

                        LazyRow {
                            items(productImages.size) { index ->
                                val isSelected = pagerState.currentPage == index

                                // **Animated Border Width & Color**
                                val borderWidth by animateDpAsState(
                                    targetValue = if (isSelected) 4.dp else 0.dp,
                                    animationSpec = tween(durationMillis = 200)
                                )

                                val borderColor by animateColorAsState(
                                    targetValue = if (isSelected) Color.Blue else Color.Transparent,
                                    animationSpec = tween(durationMillis = 200)
                                )

                                AsyncImage(
                                    model = productImages[index],
                                    contentDescription = "",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .border(
                                            width = borderWidth,
                                            color = borderColor,
                                            shape = RoundedCornerShape(8.dp)
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


                        Text(text = getProductDetailsState.value.data!!.productName)
                        Text(text = getProductDetailsState.value.data!!.productDescription)
                        Text(text = "₹${getProductDetailsState.value.data!!.productPrePrice}")
                        Text(text = "₹${getProductDetailsState.value.data!!.productFinalPrice}")
                        Text(text = "Available Units: ${getProductDetailsState.value.data!!.availableUnits}")
                        Text(
                            text = if (getProductDetailsState.value.data!!.isAvailable) "Out of Stock" else "In Stock",
                            color = if (getProductDetailsState.value.data!!.isAvailable) Color.Red else Color.Green
                        )
                    }

                    var cartClicked by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        var cartItemCount by remember { mutableIntStateOf(0) }
                        Button(onClick = {
                            cartClicked=true
                            cartItemCount++
                        }) {
                            if (cartClicked && cartItemCount>0){
                                    Row {
                                        IconButton({ cartItemCount++ }) {
                                            Icon(imageVector = Icons.Default.Add, "")
                                        }
                                        Text("$cartItemCount")
                                        IconButton({ cartItemCount-- }) {
                                            Icon(imageVector = Icons.Default.ArrowDropDown, "")
                                        }
                                        IconButton({}) {
                                            Icon(imageVector = Icons.Default.Check, "")
                                        }
                                    }
                            }
                            else{
                                Text("Add to Cart")
                            }
                        }
                        Button(onClick = {}) {
                            Text("Buy Now")
                        }
                    }
                }
            }
        }
    }
}
