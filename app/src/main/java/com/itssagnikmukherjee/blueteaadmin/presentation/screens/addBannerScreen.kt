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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
    var bannerName by remember { mutableStateOf("") }
    var bannerImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val bannerState by viewModel.addBannerState.collectAsState()
    val isLoading = bannerState.isLoading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.size in 3..5) {
            bannerImages = uris
        } else {
            Toast.makeText(context, "Please select 3 to 5 images", Toast.LENGTH_SHORT).show()
        }
    }

    // Show toast when banner is added
    LaunchedEffect(bannerState.data) {
        if (bannerState.data.isNotEmpty()) {
            Toast.makeText(context, "Banner $bannerName added successfully!", Toast.LENGTH_SHORT).show()
            bannerName = ""
            bannerImages = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Banner Name Input
        OutlinedTextField(
            value = bannerName,
            onValueChange = { bannerName = it },
            placeholder = { Text("Banner Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Image Picker Box with Preview
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bannerImages.size) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to launch image picker
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Images (3-5)")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Banner Button
        Button(
            onClick = {
                if (bannerImages.size in 3..5) {
                    viewModel.addBanner(bannerName, bannerImages, context)
                } else {
                    Toast.makeText(context, "Please select 3 to 5 images", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = bannerImages.size in 3..5 && !isLoading,
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