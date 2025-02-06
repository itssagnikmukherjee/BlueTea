package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun ProductDetailsScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController, productId: String) {
    val context = LocalContext.current
    val getProductDetailsState = viewModel.getProductDetailsState.collectAsState()

    LaunchedEffect(Unit) {
        if (productId.isNotEmpty()) {
            viewModel.getProductDetails(productId)
        } else {
            Toast.makeText(context, "Invalid Product ID", Toast.LENGTH_SHORT).show()
        }
    }

    when {
        getProductDetailsState.value.isLoading -> {
            CircularProgressIndicator()
        }
        getProductDetailsState.value.error != null -> {
            Toast.makeText(context, getProductDetailsState.value.error, Toast.LENGTH_SHORT).show()
        }
        getProductDetailsState.value.data != null -> {
            Column {
                AsyncImage(
                    model = getProductDetailsState.value.data!!.productImages[0], ""
                )

                LazyRow {
                    items(getProductDetailsState.value.data!!.productImages.size) {
                        AsyncImage(
                            model = getProductDetailsState.value.data!!.productImages[it], "",
                            modifier = Modifier.size(100.dp)
                            )
                    }
                }
                Text(text = getProductDetailsState.value.data!!.productName)
                Text(text = getProductDetailsState.value.data!!.productDescription)
                Text(text = getProductDetailsState.value.data!!.productPrePrice.toString())
                Text(text = getProductDetailsState.value.data!!.productFinalPrice.toString())
            }
        }
    }
}