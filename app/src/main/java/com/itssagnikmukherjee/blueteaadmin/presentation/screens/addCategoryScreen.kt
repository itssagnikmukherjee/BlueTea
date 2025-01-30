package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Button
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
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream

@Composable
fun AddCategoryScreen(viewModel: ViewModels = hiltViewModel()) {
    val context = LocalContext.current
    var categoryName by remember { mutableStateOf("") }
    var categoryImageUri by remember { mutableStateOf<Uri?>(null) }

    val categoryState by viewModel.addCategoryState.collectAsState()
    val isLoading = categoryState.isLoading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> categoryImageUri = uri }

    // Show toast when category added
    LaunchedEffect(categoryState.data) {
        if (categoryState.data.isNotEmpty()) {
            Toast.makeText(context, "Category $categoryName added successfully!", Toast.LENGTH_SHORT).show()
            categoryName = ""
            categoryImageUri = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Category Name Input
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            placeholder = { Text("Category Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Image Picker Box with Preview
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (categoryImageUri != null) {
                // Display the selected image
                Image(
                    painter = rememberAsyncImagePainter(categoryImageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Display a placeholder text
                Text("Select Image", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Category Button
        Button(
            onClick = {
                if (categoryImageUri != null) {
                    val category = Category(categoryName, System.currentTimeMillis())
                    viewModel.addCategory(category, categoryImageUri!!, context)
                }
            },
            enabled = categoryImageUri != null && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Add Category")
            }
        }

        // Display error message if any
        if (categoryState.error.isNotEmpty()) {
            Text(
                text = "Error: ${categoryState.error}",
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
