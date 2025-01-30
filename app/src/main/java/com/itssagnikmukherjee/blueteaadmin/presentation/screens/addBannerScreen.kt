package com.itssagnikmukherjee.blueteaadmin.presentation.screens

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels

@Composable
fun AddBannerScreen(viewModel: ViewModels = hiltViewModel()) {
    val context = LocalContext.current
    var bannerImages by remember { mutableStateOf<List<BannerImageData>>(List(3) { BannerImageData() }) }

    val bannerState by viewModel.addBannerState.collectAsState()
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

    // Show toast when banner is added
    LaunchedEffect(bannerState.data) {
        if (bannerState.data.isNotEmpty()) {
            Toast.makeText(context, "Banner added successfully!", Toast.LENGTH_SHORT).show()
            bannerImages = List(3) { BannerImageData() } // Reset to default state
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Image Boxes
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bannerImages.size) { index ->
                BannerImageBox(
                    bannerData = bannerImages[index],
                    onImageClick = { launcher.launch("image/*") },
                    onNameChange = { newName ->
                        val updatedBannerImages = bannerImages.toMutableList()
                        updatedBannerImages[index] = updatedBannerImages[index].copy(bannerName = newName)
                        bannerImages = updatedBannerImages
                    },
                    onDelete = {
                        val updatedBannerImages = bannerImages.toMutableList()
                        updatedBannerImages.removeAt(index)
                        bannerImages = updatedBannerImages
                    }
                )
            }

            // Add Button (if less than 5 images)
            if (bannerImages.size < 5) {
                item {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .clickable {
                                val updatedBannerImages = bannerImages.toMutableList()
                                updatedBannerImages.add(BannerImageData())
                                bannerImages = updatedBannerImages
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Image", tint = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Banner Button
        Button(
            onClick = {
                if (bannerImages.all { it.imageUri != null && it.bannerName.isNotEmpty() }) {
                    viewModel.addBanner(bannerImages, context)
                } else {
                    Toast.makeText(context, "Please fill all fields and select images", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = bannerImages.all { it.imageUri != null && it.bannerName.isNotEmpty() } && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Add Banner")
            }
        }

        // Display error message if any
        if (bannerState.error.isNotEmpty()) {
            Text(
                text = "Error: ${bannerState.error}",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun BannerImageBox(
    bannerData: BannerImageData,
    onImageClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onDelete: () -> Unit // Add a callback for deleting the image box
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Box with Delete Button
            Box(
                modifier = Modifier
                    .size(150.dp)
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
                    Text("Select Image", color = Color.Gray)
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
            OutlinedTextField(
                value = bannerData.bannerName,
                onValueChange = onNameChange,
                placeholder = { Text("Banner Name") }, // Indicate that the field is optional
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

data class BannerImageData(
    val imageUri: Uri? = null,
    val bannerName: String = ""
)