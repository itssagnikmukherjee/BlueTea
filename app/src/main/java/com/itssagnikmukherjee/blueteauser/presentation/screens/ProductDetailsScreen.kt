package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun ProductDetailsScreen(viewModel : ViewModels = hiltViewModel(), navController: NavController, productId: String) {
    val context = LocalContext.current
    val getProductDetailsState = viewModel.getProductDetailsState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getProductDetails(productId)
    }

    when{
        getProductDetailsState.value.isLoading ->{
            CircularProgressIndicator()
        }
        getProductDetailsState.value.error.isNotEmpty() ->{
            Toast.makeText(context, getProductDetailsState.value.error, Toast.LENGTH_SHORT).show()
        }
        getProductDetailsState.value.data != null ->{
            Column {
                Text(text = getProductDetailsState.value.data.toString())
            }
        }
    }

}

//TODO
@Composable
fun ProductDetailsSection() {

}