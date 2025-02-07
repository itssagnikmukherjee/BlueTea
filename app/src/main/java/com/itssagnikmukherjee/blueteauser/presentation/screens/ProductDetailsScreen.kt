package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
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
            var selectedImage by remember { mutableStateOf(getProductDetailsState.value.data?.productImages[0]) }
            Box {
                IconButton(onClick = {
                    navController.popBackStack()
                }, modifier = Modifier.zIndex(999f)) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                }
                Column {
                    AsyncImage(
                        model = selectedImage, "",
                        modifier = Modifier.size(400.dp)
                    )

                    LazyRow {
                        items(getProductDetailsState.value.data!!.productImages.size) {
                            AsyncImage(
                                model = getProductDetailsState.value.data!!.productImages[it], "",
                                modifier = Modifier.size(100.dp)
                                    .border(
                                        width = if (selectedImage == getProductDetailsState.value.data!!.productImages[it]) 4.dp
                                        else 0.dp,
                                        color = if (selectedImage == getProductDetailsState.value.data!!.productImages[it]) Color.Blue else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedImage =
                                            getProductDetailsState.value.data!!.productImages[it]
                                    }
                            )
                        }
                    }
                    Text(text = getProductDetailsState.value.data!!.productName)
                    Text(text = getProductDetailsState.value.data!!.productDescription)
                    Text(text = getProductDetailsState.value.data!!.productPrePrice.toString())
                    Text(text = getProductDetailsState.value.data!!.productFinalPrice.toString())
                    Text(text = getProductDetailsState.value.data!!.availableUnits.toString())
                    if (getProductDetailsState.value.data!!.isAvailable) {
                        Text(text = "Out of Stock")
                    } else {
                        Text(text = "In Stock")
                    }
                }
            }
        }
    }
}