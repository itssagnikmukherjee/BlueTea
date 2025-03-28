package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.R.attr.x
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomActionButton
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors.primaryBlack
import com.itssagnikmukherjee.blueteauser.presentation.theme.headingTextStyle

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUser(modifier: Modifier = Modifier, viewmodel: ViewModels = hiltViewModel(), navController: NavController) {

    val firebaseAuth = FirebaseAuth.getInstance()
    val userId = firebaseAuth.currentUser?.uid ?: ""
    Log.d("HomeScreenUser", "User ID: $userId")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(.95f)
                            .clip(RoundedCornerShape(40.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Search Products", color = Color.Gray, fontFamily = fontFamily, fontSize = 16.sp,textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CustomColors.lightGray, shape = RoundedCornerShape(50)),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.Gray,
                            )
                        )
                        Icon(painter = painterResource(R.drawable.search),
                            contentDescription = "Search",
                            tint = CustomColors.mediumGray, modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
                                .offset(x = (-30).dp).clickable{}
                        )
                    }
                }
            )
        }
    ){ innerPadding->

        val categoryState by viewmodel.getCategoryState.collectAsState()
        val bannerState by viewmodel.getBannerState.collectAsState()
        val productState by viewmodel.getProductState.collectAsState()

        LaunchedEffect(Unit) {
            viewmodel.getCategories()
            viewmodel.getBanners()
            viewmodel.getProducts()
        }

        if(categoryState.isLoading || bannerState.isLoading || productState.isLoading) { ShimmerScreen() } else
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(Modifier.height(20.dp))
            // Banner Carousel
            AnimatedBannerSection(banners = bannerState.data, viewModels = viewmodel)


            // Category List
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(start = 20.dp)) {
                items(categoryState.data.size) { index ->
                    CategoryItem(category = categoryState.data[index]!!)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text("Hot Deals", style = headingTextStyle, modifier = Modifier.padding(start = 20.dp))
            Spacer(modifier = Modifier.height(18.dp))
            // Products List
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(productState.data.size) { product ->
                    ProductItem(
                        product = productState.data[product],
                        onclick = {
                            val productId = productState.data[product].productId
                            if (productId.isNotEmpty()) {
                                navController.navigate(Routes.ProductDetailsScreen(productId, userId))
                            } else {
                                Log.e("HomeScreenUser", "Invalid productId: $productId")
                            }
                        },
                        userId = userId,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = "Category Image",
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(CustomColors.lightGray).border(3.dp, CustomColors.mediumGray, CircleShape),
            contentScale = ContentScale.Crop
        )
        Text(
            text = category.categoryName,
            modifier = Modifier.padding(top = 5.dp),
            textAlign = TextAlign.Center,
            fontFamily = fontFamily,
            fontSize = 16.sp,
            color = primaryBlack
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimatedBannerSection(
    modifier: Modifier = Modifier,
    banners: List<Banner>,
    viewModels: ViewModels
) {
    var settings by remember { mutableStateOf(BannerAnimationSettings()) }
    val allImages = banners.flatMap { it.bannerImageUrls }

    val pagerState = rememberPagerState(pageCount = { allImages.size })
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

    val autoAdvance = !pagerIsDragged || settings.isLooping
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    var scaleCount = 2

    LaunchedEffect(Unit) {
        viewModels.fetchBannerSettings{
            settings = it
        }
    }

    LaunchedEffect(viewModels) {
        snapshotFlow { viewModels.bannerSettingsState.value }
            .collectLatest { newSettings ->
                newSettings?.let { settings = it }
            }
    }

    if (autoAdvance) {
        LaunchedEffect(pagerState, settings) {
            while (true) {
                delay(settings.duration.toLong())
                val nextPage = (pagerState.currentPage + 1) % allImages.size
                pagerState.animateScrollToPage(nextPage)
            }
        }

        LaunchedEffect(scale, settings) {
            while (scaleCount != 0 && settings.animationType == "Zoom") {
                scale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = tween(durationMillis = settings.duration / 2, easing = FastOutSlowInEasing)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(settings.duration / 2, easing = FastOutSlowInEasing)
                )
                scaleCount--
            }
        }

        LaunchedEffect(alpha, settings) {
            if (settings.animationType == "Fade") {
                while (true) {
                    alpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = settings.duration / 10, easing = FastOutSlowInEasing)
                    )
                    alpha.animateTo(
                        targetValue = 0.8f,
                        animationSpec = tween(settings.duration, easing = FastOutSlowInEasing)
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                pageSpacing = 20.dp
            ) { page ->
                val imageUrl = allImages[page]

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Banner Image",
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = alpha.value
                        ),
                    contentScale = ContentScale.Crop
                )
            }
            PagerIndicator(allImages.size, pagerState.currentPage)
        }
    }
}

data class BannerAnimationSettings(
    val animationType: String = "Fade",
    val duration: Int = 1000,
    val isLooping: Boolean = false
)

@Composable
fun PagerIndicator(pageCount: Int, currentPageIndex: Int, modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (currentPageIndex == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)
                )
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, onclick: () -> Unit, viewModel: ViewModels = hiltViewModel(), userId : String, navController: NavController) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()

    LaunchedEffect(userId.isNotEmpty()) {
        viewModel.getUserDetails(userId)
    }

    var isFavorite by remember(getUserDetailsState.value.data) {
        mutableStateOf(getUserDetailsState.value.data?.wishlistItems?.contains(product.productId) ?: false)
    }

    var isCarted by remember(getUserDetailsState.value.data) {
        mutableStateOf(getUserDetailsState.value.data?.cartItems?.containsKey(product.productId) ?: false)
    }
    Card(
        modifier = Modifier
            .width(160.dp).height(320.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .zIndex(999f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomActionButton(onClick = {
                    isFavorite = !isFavorite
                    viewModel.updateFavoriteList(
                        userId = userId,
                        productId = product.productId,
                        isFavorite = isFavorite
                    )
                }, icon = if (isFavorite) R.drawable.heart_solid else R.drawable.heart_regular, contentDescription = "Favorite")

                Spacer(modifier = Modifier.height(8.dp))

                CustomActionButton(onClick = {
                    isCarted = !isCarted
                        viewModel.updateCartList(
                            userId = userId,
                            productId = product.productId,
                            isCarted = isCarted,
                            quantity = 1
                        )
                }, icon = if (isCarted) R.drawable.cart_filled else R.drawable.cart_outlined, contentDescription = "Cart")
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onclick() },
                horizontalAlignment = Alignment.Start
            ) {
                Box{
                    if (product.productImages.isNotEmpty()) {
                        AsyncImage(
                            model = product.productImages[0],
                            contentDescription = "Product Image",
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp)).size(160.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color.LightGray, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No Image",
                                color = Color.Gray
                            )
                        }
                    }

                    val prePrice = product.productPrePrice.toFloat()
                    val finalPrice = product.productFinalPrice.toFloat()
                    val discountPercentage = ((prePrice - finalPrice) / prePrice * 100).toInt()

                    Box(Modifier.padding(10.dp).clip(CircleShape).background(CustomColors.primaryBlack).size(30.dp).zIndex(999f).align(Alignment.BottomStart),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "$discountPercentage%",
                            fontFamily = fontFamily,
                            color = Color.White,
                            fontSize = 12.sp,
                        )
                    }

                }

                Spacer(modifier = Modifier.height(6.dp))
                Column(Modifier.padding(start = 10.dp)){
                    Text(
                        text = product.productName,
                        fontFamily = fontFamily,
                        color = primaryBlack,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                    )

                    Text(
                        text = product.productCategory,
                        fontFamily = fontFamily,
                        color = primaryBlack,
                        fontSize = 12.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Light,
                        overflow = TextOverflow.Ellipsis
                    )

                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                    text = "₹",
                    fontFamily = fontFamily,
                    color = CustomColors.primaryBlack,
                    textDecoration = TextDecoration.LineThrough,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ){
                        Text(
                            text = "${product.productFinalPrice}",
                            fontFamily = fontFamily,
                            color = primaryBlack,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${product.productPrePrice}",
                            fontFamily = fontFamily,
                            color = primaryBlack,
                            textDecoration = TextDecoration.LineThrough,
                            fontSize = 12.sp
                        )
                    }
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ){
                        //random value from 4-5 with point values
                        val randomVal = (40..50).random() / 10.0
                        Icon(painter = painterResource(R.drawable.star_rating), contentDescription = "Rating", modifier = Modifier.size(16.dp), tint = Color(0xFFFFAB62))
                        Text(randomVal.toString(), fontFamily = fontFamily, color = primaryBlack, fontSize = 12.sp)
                    }
                }
            }
            }
        }
    }
}