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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

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
    val alpha = remember { Animatable(1f) } // 🔹 Added for Fade effect
    var scaleCount = 2

    // 🔹 Fetch banner settings when Composable loads
    LaunchedEffect(Unit) {
        viewModels.fetchBannerSettings{
            settings = it
        }
    }

    // 🔄 Observe settings changes dynamically
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

        // 🔹 Zoom Animation
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

        // 🔹 Fade Animation
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