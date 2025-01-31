package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

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
        BannerSection(banners = bannerState.data)

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
fun BannerSection(
    banners: List<Banner>,
    modifier: Modifier = Modifier
) {
    if (banners.isEmpty()) {
        CircularProgressIndicator()
        return
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { banners.size },
        modifier = Modifier.fillMaxWidth(),
        preferredItemWidth = 180.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index ->
        val banner = banners[index]

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ✅ LazyRow to display all images for this banner
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(banner.bannerImageUrls.size) { imgIndex ->
                    AsyncImage(
                        model = banner.bannerImageUrls[imgIndex],
                        contentDescription = "Banner Image",
                        modifier = Modifier
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.large),
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }
    }
}

