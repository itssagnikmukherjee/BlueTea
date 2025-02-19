package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun HomeScreenUser(modifier: Modifier = Modifier, viewmodel: ViewModels = hiltViewModel(), navController: NavController) {

    val firebaseAuth = FirebaseAuth.getInstance()
    val userId = firebaseAuth.currentUser?.uid ?: ""
    Log.d("HomeScreenUser", "User ID: $userId")

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ){ innerPadding->

        val categoryState by viewmodel.getCategoryState.collectAsState()
        val bannerState by viewmodel.getBannerState.collectAsState()
        val productState by viewmodel.getProductState.collectAsState()

        LaunchedEffect(Unit) {
            viewmodel.getCategories()
            viewmodel.getBanners()
            viewmodel.getProducts()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Banner Carousel
            AnimatedBannerSection(banners = bannerState.data, viewModels = viewmodel)

            Spacer(modifier = Modifier.height(16.dp))

            // Category List
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categoryState.data.size) { index ->
                    CategoryItem(category = categoryState.data[index]!!)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                .clip(RoundedCornerShape(50))
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Text(
            text = category.categoryName,
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
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
    if (banners.isEmpty()) {
        CircularProgressIndicator()
        return
    }

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
                delay(settings.duration.toLong()) // ⏳ Apply dynamic duration
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
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
    ) {
        Column {
            HorizontalPager(
                state = pagerState,
                modifier = modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
            ) { page ->
                val imageUrl = allImages[page]

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Banner Image",
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(30.dp))
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
    Box(modifier = Modifier.fillMaxWidth()) {
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
                        .size(16.dp)
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

    var isCarted by remember  (getUserDetailsState.value.data){
        mutableStateOf(getUserDetailsState.value.data?.cartItems?.contains(product.productId)?:false)
    }

    Card {
        Box {
            Column(
                modifier = Modifier.align(Alignment.TopEnd).zIndex(999f)
            ) {

                IconButton(onClick = {
                    isFavorite = !isFavorite
                    viewModel.updateFavoriteList(
                        userId = userId,
                        productId = product.productId,
                        isFavorite = isFavorite
                    )
                }) {
                    Icon(imageVector = if (!isFavorite) Icons.Default.FavoriteBorder else Icons.Default.Favorite,
                        contentDescription = "")
                }

                IconButton(onClick={
                    isCarted = !isCarted
                    viewModel.updateCartList(
                        userId = userId,
                        productId = product.productId,
                        isCarted = isCarted,
                        quantity = 1
                    )
                }) {
                    Icon(imageVector = if(!isCarted) Icons.Default.ShoppingCart else Icons.Default.MailOutline,"")
                }
            }

            Column(
                modifier = Modifier.clickable {
                    onclick()
                }
            ) {
                if (product.productImages.isNotEmpty()) {
                    AsyncImage(
                        model = product.productImages[0],
                        contentDescription = "Product Image",
                        modifier = Modifier.size(100.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No Image")
                    }
                }
                Text(text = product.productName)
                Text(text = product.productDescription)
                Text(text = "Original Price: $${product.productPrePrice}")
                Text(text = "Discounted Price: $${product.productFinalPrice}")
                Button(onClick = {
                    navController.navigate(
                        Routes.BuyNowScreen(
                        products = listOf(product.productId),
                        totalPrice = product.productFinalPrice.toDouble(),
                        userId = userId,
                        quantity = Json.encodeToString(mapOf(product.productId to 1))
                        )
                    )
                }){
                    Text("Buy Now")
                }
            }
        }
    }
}