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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
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
import kotlin.collections.get

@Composable
fun AddCategoryScreen(viewModel: ViewModels = hiltViewModel()) {
    val context = LocalContext.current
    var categoryName by remember { mutableStateOf("") }
    var categoryImageUri by remember { mutableStateOf<Uri?>(null) }

    val categoryState by viewModel.addCategoryState.collectAsState()
    val deleteCategoryState by viewModel.deleteCategoryState.collectAsState() // Observe deletion state
    val getCategoriesState by viewModel.getCategoryState.collectAsState()
    val isLoading = categoryState.isLoading

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> categoryImageUri = uri }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    // Show toast when category added
    LaunchedEffect(categoryState.data) {
        if (categoryState.data.isNotEmpty()) {
            Toast.makeText(context, "Category $categoryName added successfully!", Toast.LENGTH_SHORT).show()
            categoryName = ""
            categoryImageUri = null
        }
    }

    // Show toast when category deleted
    LaunchedEffect(deleteCategoryState.data) {
        if (deleteCategoryState.data.isNotEmpty()) {
            Toast.makeText(context, deleteCategoryState.data, Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Text("Current Preview")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(getCategoriesState.data.size) { index ->
                CategoryItem(category = getCategoriesState.data[index]!!)
            }
        }

        // Add Category
        Text("Add New Category")
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Picker Box with Preview
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color.LightGray, CircleShape)
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
                    Text("Select Image", color = Color.Gray)
                }
            }

            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                placeholder = { Text("Category Name") },
                modifier = Modifier.fillMaxWidth()
            )

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

        Text("Edit Categories")
        EditCategoryItems(getCategoriesState.data, viewModel, context)
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

@Composable
fun EditCategoryItems(categories: List<Category?>, viewModel: ViewModels, context: Context) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Handle image update (to be implemented later)
        }
    }

    LazyRow {
        items(categories.size) { index ->
            val category = categories[index]
            if (category != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = category.imageUrl,
                                contentDescription = "Category Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete Category",
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .clickable {
                                        // Delete the category by name
                                        viewModel.deleteCategory(category.categoryName)
                                    }
                                    .padding(4.dp)
                                    .size(24.dp),
                                tint = Color.Red
                            )
                        }
                        Text(
                            text = category.categoryName,
                            modifier = Modifier.padding(top = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}