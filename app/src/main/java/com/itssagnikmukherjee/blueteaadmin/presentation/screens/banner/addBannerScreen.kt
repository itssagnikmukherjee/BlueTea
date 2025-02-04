package com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
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
            bannerImages = List(3) { BannerImageData() }
        }
    }
    LaunchedEffect(Unit) { viewModel.getBanners() }

    val hasChanges = bannerImages != initialBannerImages

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Current Preview")
        Spacer(modifier = Modifier.height(20.dp))
        AnimatedBannerSection(banners = getBannerState.data, viewModels = viewModel)
        Spacer(modifier = Modifier.height(20.dp))
        Text("Edit Banners")
        Spacer(modifier = Modifier.height(20.dp))

        Column {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(bannerImages.size) { index ->
                    BannerImageBox(
                        bannerData = bannerImages[index],
                        onImageClick = { launcher.launch("image/*") },
                        onNameChange = { newName ->
                            val updatedBannerImages = bannerImages.toMutableList()
                            updatedBannerImages[index] =
                                updatedBannerImages[index].copy(bannerName = newName)
                            bannerImages = updatedBannerImages
                        },
                        onDelete = {
                            val updatedBannerImages = bannerImages.toMutableList()
                            updatedBannerImages.removeAt(index)
                            bannerImages = updatedBannerImages
                        },
                        bannerIdx = index + 1
                    )
                }

                if (bannerImages.size < 5) {
                    item {
                        Box(
                            modifier = Modifier
                                .width(300.dp)
                                .height(200.dp)
                                .background(Color.LightGray, RoundedCornerShape(8.dp))
                                .clickable {
                                    val updatedBannerImages = bannerImages.toMutableList()
                                    updatedBannerImages.add(BannerImageData())
                                    bannerImages = updatedBannerImages
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Image",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (bannerImages.all { it.imageUri != null && it.bannerName.isNotEmpty() }) {
                        viewModel.addBanner(bannerImages, context)
                    } else {
                        Toast.makeText(
                            context,
                            "Please fill all fields and select images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = bannerImages.all { it.imageUri != null } && !isLoading && hasChanges,
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text(if (hasChanges) "Update Banners" else "Add Banner")
                }
            }
        }
        AdminBannerSettingsScreen(viewModel)
    }
}




@Composable
fun AdminBannerSettingsScreen(viewModel: ViewModels) {
    var selectedAnimation by remember { mutableStateOf("Fade") }
    var duration by remember { mutableStateOf(1000) } //
    var isLooping by remember { mutableStateOf(false) }
    var expandedAnimation by remember { mutableStateOf(false) }
    var expandedDuration by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row {
        Text("Select Animation")
            Box {
                Button(onClick = { expandedAnimation = true }) {
                    Text(selectedAnimation)
                }
                DropdownMenu(expanded = expandedAnimation, onDismissRequest = { expandedAnimation = false }) {
                    listOf("Fade", "Slide", "Zoom").forEach { animation ->
                        DropdownMenuItem(
                            text = { Text(animation) },
                            onClick = {
                                selectedAnimation = animation
                                expandedAnimation = false
                            }
                        )
                    }
                }
            }
        }

        Row{
        Text("Duration: ")
            Box {
                Button(onClick = { expandedDuration = true }) {
                    Text("${duration} s")
                }
                DropdownMenu(expanded = expandedDuration, onDismissRequest = { expandedDuration = false }) {
                    listOf(1, 2, 3, 4, 5).forEach { time ->
                        DropdownMenuItem(
                            text = { Text("${time} s") },
                            onClick = {
                                duration = time
                                expandedDuration = false
                            }
                        )
                    }
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable Looping")
            Switch(checked = isLooping, onCheckedChange = { isLooping = it })
        }

        Button(
            onClick = {
                viewModel.saveBannerSettings(BannerAnimationSettings(selectedAnimation, duration * 1000, isLooping))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Settings")
        }
    }
}



@Composable
fun BannerImageBox(
    bannerIdx: Int,
    bannerData: BannerImageData,
    onImageClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDelete: () -> Unit // Add a callback for deleting the image box
) {
    Box(
        modifier = Modifier.fillMaxWidth().width(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()
        ) {
            // Image Box with Delete Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(8.dp))
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

                // Delete Button (Cross Icon)
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete Image",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clickable { onDelete() },
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Banner Name Text Field
            if(bannerData.imageUri != null) {
                TextField(
                value = bannerData.bannerName,
                onValueChange = onNameChange,
                placeholder = { Text("Banner Name") }, // Indicate that the field is optional
                modifier = Modifier.fillMaxWidth().offset(y=-10.dp).clip(RoundedCornerShape(0.dp,10.dp,20.dp,20.dp))
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
