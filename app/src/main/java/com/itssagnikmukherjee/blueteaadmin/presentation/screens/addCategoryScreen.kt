package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


@Composable
fun AddCategoryScreen(viewModels: ViewModels = hiltViewModel()) {
    val context = LocalContext.current

    var categoryName by remember { mutableStateOf("") }
    var categoryImageUrl by remember { mutableStateOf("") }


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(value = categoryName, onValueChange = { categoryName = it }, placeholder = { Text("Category Name") })
        OutlinedTextField(value = categoryImageUrl, onValueChange = { categoryImageUrl = it }, placeholder = { Text("Category Img") })
        Button(onClick = {
            viewModels.addCategory(Category(categoryName, System.currentTimeMillis(), categoryImageUrl))
            Toast.makeText(context, "Category $categoryName Added", Toast.LENGTH_SHORT).show()
        }, enabled = categoryImageUrl.isNotEmpty()) { Text("Add Category") }
    }
}