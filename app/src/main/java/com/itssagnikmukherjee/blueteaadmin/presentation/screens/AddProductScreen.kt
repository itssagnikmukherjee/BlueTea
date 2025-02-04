package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels

@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: ViewModels = hiltViewModel()) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var prePrice by remember { mutableIntStateOf(0) }
    var finalPrice by remember { mutableIntStateOf(0) }
    var productCategory by remember { mutableStateOf("") }
    var productImageUri by remember { mutableStateOf<Uri?>(null) }
    var availableUnits by remember { mutableIntStateOf(0) }
    val productState by viewModel.addProductState.collectAsState()
    val isLoading = productState.isLoading
    val getCategoryState by viewModel.getCategoryState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){uri:Uri? ->
        productImageUri = uri
    }

    Column {
        LazyRow {
            items(getCategoryState.data.size) {index->
                Text(text = getCategoryState.data[index]?.categoryName ?: "")
            }
        }
        Text("Add Product")
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") }
        )
        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Product Description") }
        )
        OutlinedTextField(
            value = prePrice.toString(),
            onValueChange = { prePrice = it.toIntOrNull() ?: 0 },
            label = { Text("Price") }
        )
        if(prePrice!=0){
            OutlinedTextField(
                value = finalPrice.toString(),
                onValueChange = { finalPrice = it.toIntOrNull() ?: 0 },
                label = { Text("Discount Price") }
            )
        }
        OutlinedTextField(
            value = productCategory,
            onValueChange = { productCategory = it },
            label = { Text("Product Category") }
        )
        OutlinedTextField(
            value = availableUnits.toString(),
            onValueChange = { availableUnits = it.toIntOrNull() ?: 0 },
            label = { Text("Available Units") }
        )
    }


}