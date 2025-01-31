package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.presentation.GetCategoryState
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels


@Composable
fun HomeScreenUser(modifier: Modifier = Modifier, viewmodel: ViewModels = hiltViewModel()) {
    val categoryState = viewmodel.getCategoryState.collectAsState()
    val bannerState = viewmodel.getBannerState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewmodel.getCategories()
        viewmodel.getBanners()
    }

    Column(modifier= Modifier.fillMaxSize()) {

        BannerSection(bannerState.value.data)

        LazyRow() {
            items(categoryState.value.data.size){
                    items->
                CategoryItem(categoryState as MutableState<GetCategoryState>,items)
            }
        }


    }



}

@Composable
fun CategoryItem(state: MutableState<GetCategoryState>, it: Int) {
    Column(modifier = Modifier.padding(10.dp)){
        AsyncImage("${state.value.data[it]?.imageUrl}","category_img",
            modifier = Modifier.size(90.dp)
                .clip(RoundedCornerShape(50))
                .wrapContentSize(Alignment.Center,false),
        )
        Text("${state.value.data[it]?.categoryName}",
            modifier = Modifier.padding(5.dp).align(Alignment.CenterHorizontally)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BannerSection(
    banners: List<Banner>,
    modifier: Modifier = Modifier
) {
    val animatedScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            animatedScale.animateTo(
                targetValue = 1.2f,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
            animatedScale.animateTo(
                targetValue = 1.1f,
                animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
            )
        }
    }

    HorizontalMultiBrowseCarousel(
        state = rememberCarouselState { banners.size },
        modifier = Modifier.size(200.dp),
        preferredItemWidth = 186.dp,
        itemSpacing = 8.dp,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) { index ->
        val banner = banners[index]
        Column {
            // Display all images for the banner
            banner.bannerImageUrls.forEach { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .height(205.dp)
                        .maskClip(MaterialTheme.shapes.extraLarge)
                        .graphicsLayer(
                            scaleX = animatedScale.value,
                            scaleY = animatedScale.value
                        ),
                    contentScale = ContentScale.Crop
                )
            }
            // Display the banner name
            Text(
                text = banner.bannerName,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}