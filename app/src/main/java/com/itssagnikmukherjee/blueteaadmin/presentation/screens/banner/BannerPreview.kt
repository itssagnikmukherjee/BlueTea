package com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import kotlinx.coroutines.delay

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