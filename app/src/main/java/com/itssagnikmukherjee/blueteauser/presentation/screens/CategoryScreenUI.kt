package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun CategoryScreen(category: String, viewModel: ViewModels = hiltViewModel()){

    val getProductState = viewModel.getProductState.collectAsState()
    val productData = getProductState.value.data
    val categoryProduct = productData.filter { it.productCategory == category }

    LaunchedEffect(Unit) {
        viewModel.getProducts()
    }
    Log.d("CATEGORY SCREEN", "CATEGORY: $category")
    Log.d("CATEGORY SCREEN", "PRODUCTS: ${categoryProduct}")

    Column(Modifier.fillMaxSize()){
        Text("showing all for $category category")
        Text("${productData.size} products")
    }
}