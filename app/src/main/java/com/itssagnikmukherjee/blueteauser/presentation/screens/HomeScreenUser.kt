package com.itssagnikmukherjee.blueteauser.presentation.screens

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
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreenUser(modifier: Modifier = Modifier, viewmodel: ViewModels = hiltViewModel()) {
    val categoryState by viewmodel.getCategoryState.collectAsState()
    val bannerState by viewmodel.getBannerState.collectAsState()

    LaunchedEffect(Unit) {
        viewmodel.getCategories()
        viewmodel.getBanners()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Banner Carousel
        AnimatedBannerSection(banners = bannerState.data)


        Spacer(modifier = Modifier.height(16.dp))

        // Category List
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(categoryState.data.size) { index ->
                CategoryItem(category = categoryState.data[index]!!)
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
    banners: List<Banner>
) {
    if (banners.isEmpty()) {
        CircularProgressIndicator()
        return
    }


    val allImages = banners.flatMap { it.bannerImageUrls }

    val pagerState = rememberPagerState(pageCount = { allImages.size })
    val pagerIsDragged by pagerState.interactionSource.collectIsDraggedAsState()

    val autoAdvance = !pagerIsDragged
    val scale = remember { Animatable(1f) }
    var scaleCount = 2

    if (autoAdvance) {
        LaunchedEffect(pagerState) {
            while (true) {
                delay(4000)
                val nextPage = (pagerState.currentPage + 1) % allImages.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
        LaunchedEffect(scale) {
            while(scaleCount!=0){
                scale.animateTo(
                    targetValue = 1.1f,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(1000, easing = FastOutSlowInEasing)
                )
                scaleCount--
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
                    modifier = Modifier.height(200.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .graphicsLayer(scaleX = scale.value, scaleY = scale.value),
                    contentScale = ContentScale.Crop
                )
            }
            PagerIndicator(allImages.size, pagerState.currentPage)
        }
    }
}

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


