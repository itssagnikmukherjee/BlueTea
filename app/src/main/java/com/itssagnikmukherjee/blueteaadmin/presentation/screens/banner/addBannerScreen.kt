package com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.R
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.ShimmerScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack
import kotlin.text.indexOf


@Composable
fun AddBannerScreen(viewModel: ViewModels = hiltViewModel()) {
    val context = LocalContext.current
    var bannerImages by remember { mutableStateOf<List<BannerImageData>>(List(3) { BannerImageData() }) }
    val initialBannerImages by remember { mutableStateOf(bannerImages) }

    val bannerState by viewModel.addBannerState.collectAsState()
    val getBannerState by viewModel.getBannerState.collectAsState()
    val isLoading = bannerState.isLoading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            val updatedBannerImages = bannerImages.toMutableList()
            val emptyBannerIndex = updatedBannerImages.indexOfFirst { it.imageUri == null }
            if (emptyBannerIndex != -1) {
                updatedBannerImages[emptyBannerIndex] = updatedBannerImages[emptyBannerIndex].copy(imageUri = selectedUri)
                bannerImages = updatedBannerImages
            }
        }
    }

    LaunchedEffect(bannerState.data) {
        if (bannerState.data.isNotEmpty()) {
            Toast.makeText(context, "Banner updated successfully!", Toast.LENGTH_SHORT).show()
            bannerImages = List(bannerImages.size) { BannerImageData() }
        }
    }
    LaunchedEffect(Unit) { viewModel.getBanners() }

    val hasChanges = bannerImages != initialBannerImages
    val hasValidBanners = bannerImages.all { it.imageUri != null && it.bannerName.isNotEmpty() }
    val shouldShowUpdateButton = hasChanges && hasValidBanners && !isLoading
    if(getBannerState.isLoading){
        ShimmerScreen()
    }else
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val allImages = getBannerState.data.flatMap { it.bannerImageUrls }
        Box(
            Modifier
                .width(110.dp)
                .height(50.dp)
        ){
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
            ){
                Text(
                    "${allImages.size}",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(primaryBlack)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Banners",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart),
                color = primaryBlack
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedBannerSection(banners = getBannerState.data, viewModels = viewModel)
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            Modifier
                .width(160.dp)
                .height(40.dp)
        ){
            Text(
                text = "Modify Banners",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart),
                color = primaryBlack
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column {
            Box (Modifier.clip(RoundedCornerShape(20.dp))) {
                AnimatedBannerGrid(
                    bannerImages = bannerImages,
                    onImageClick = { launcher.launch("image/*") },
                    onNameChange = { index, newName ->
                        val updatedBannerImages = bannerImages.toMutableList()
                        updatedBannerImages[index] = updatedBannerImages[index].copy(bannerName = newName)
                        bannerImages = updatedBannerImages
                    },
                    onDelete = { index ->
                        val updatedBannerImages = bannerImages.toMutableList()
                        updatedBannerImages.removeAt(index)
                        bannerImages = updatedBannerImages
                    },
                    onAdd = {
                        val updatedBannerImages = bannerImages.toMutableList()
                        updatedBannerImages.add(BannerImageData())
                        bannerImages = updatedBannerImages
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedVisibility(
                visible = shouldShowUpdateButton,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn() + scaleIn(initialScale = 0.8f),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut() + scaleOut()
            ) {
                Button(
                    onClick = {
                        viewModel.addBanner(bannerImages, context)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Update Banners")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        AdminBannerSettingsScreen(viewModel)
    }
}

@Composable
fun AnimatedBannerGrid(
    bannerImages: List<BannerImageData>,
    onImageClick: () -> Unit,
    onNameChange: (Int, String) -> Unit,
    onDelete: (Int) -> Unit,
    onAdd: () -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(bannerImages.size) { index ->
            androidx.compose.animation.AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ) + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                BannerImageBox(
                    bannerData = bannerImages[index],
                    onImageClick = onImageClick,
                    onNameChange = { onNameChange(index, it) },
                    onDelete = { onDelete(index) },
                    bannerIdx = index + 1
                )
            }
        }

        if (bannerImages.size < 5) {
            item {
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    ) + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .width(100.dp)
                            .height(200.dp)
                            .background(Color.LightGray, RoundedCornerShape(20.dp))
                            .clickable { onAdd() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Image",
                            tint = primaryBlack
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminBannerSettingsScreen(viewModel: ViewModels) {
    var selectedAnimation by remember { mutableStateOf("Fade") }
    var duration by remember { mutableStateOf(1000) }
    var isLooping by remember { mutableStateOf(false) }
    var expandedAnimation by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }
    var initialSettings by remember {
        mutableStateOf(BannerAnimationSettings(selectedAnimation, duration, isLooping))
    }

    val hasSettingsChanged by remember {
        derivedStateOf {
            BannerAnimationSettings(selectedAnimation, duration, isLooping) != initialSettings
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth().height(350.dp)
            .padding(vertical = 8.dp),
    ) {
        Column {
            Text(
                "Banner Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = primaryBlack,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Animation", fontSize = 16.sp)
                Box {
                    Button(
                        onClick = { expandedAnimation = true },
                        modifier = Modifier.width(150.dp).offset(y = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlack)
                    ) {
                        Text(selectedAnimation, fontFamily = fontFamily)
                    }
                    DropdownMenu(
                        modifier = Modifier.width(150.dp),
                        expanded = expandedAnimation,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 0.dp,
                        onDismissRequest = { expandedAnimation = false }
                    ) {
                        listOf("Fade", "Slide", "Zoom").forEach { animation ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        animation,
                                        fontFamily = fontFamily,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = primaryBlack
                                    )
                                },
                                onClick = {
                                    selectedAnimation = animation
                                    expandedAnimation = false
                                },
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Duration", fontSize = 16.sp)
                Box {
                    Button(
                        onClick = { expandedDuration = true },
                        modifier = Modifier.width(150.dp).offset(y = 10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlack)
                    ) {
                        Text("${duration / 1000} s", fontFamily = fontFamily)
                    }
                    DropdownMenu(
                        modifier = Modifier.width(150.dp),
                        expanded = expandedDuration,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 0.dp,
                        onDismissRequest = { expandedDuration = false }
                    ) {
                        listOf(1, 2, 3, 4, 5).forEach { time ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "${time} s",
                                        fontFamily = fontFamily,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = primaryBlack
                                    )
                                },
                                onClick = {
                                    duration = time * 1000
                                    expandedDuration = false
                                },
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Enable Looping", fontSize = 16.sp)
                Switch(
                    checked = isLooping,
                    onCheckedChange = { isLooping = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = primaryBlack,
                        checkedTrackColor = Color.White,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedBorderColor = Color.LightGray,
                        checkedBorderColor = primaryBlack
                    )
                )
            }
            val context = LocalContext.current
            AnimatedVisibility(
                visible = hasSettingsChanged,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }) + scaleIn(
                    initialScale = 0.8f
                ),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }) + scaleOut(
                    targetScale = 0.8f
                )
            )
            {
                Button(
                    onClick = {
                        val newSettings =
                            BannerAnimationSettings(selectedAnimation, duration, isLooping)
                        viewModel.saveBannerSettings(newSettings)
                        initialSettings = newSettings
                        Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth().height(70.dp)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlack),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Save Settings", fontFamily = fontFamily)
                        Icon(
                            Icons.Default.Done,
                            contentDescription = "Save Settings",
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BannerImageBox(
    bannerIdx: Int,
    bannerData: BannerImageData,
    onImageClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(20.dp))
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (bannerData.imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(bannerData.imageUri),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Select Image $bannerIdx", color = Color.Gray)
                }
                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = primaryBlack,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trash_solid),
                        contentDescription = "Delete Image",
                        modifier = Modifier
                            .size(18.dp),
                        tint = Color.White
                    )
                }
            }

            if(bannerData.imageUri != null) {
                TextField(
                    value = bannerData.bannerName,
                    onValueChange = onNameChange,
                    placeholder = { Text("Banner Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = -10.dp)
                        .clip(RoundedCornerShape(0.dp, 10.dp, 20.dp, 20.dp))
                )
            }
        }
    }
}


data class BannerImageData(
    val imageUri: Uri? = null,
    val bannerName: String = ""
)

data class BannerAnimationSettings(
    val animationType: String = "Fade",
    val duration: Int = 1000,
    val isLooping: Boolean = false
)