package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomActionButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryScreen(category: String, navController: NavController, userId: String, viewModel: ViewModels = hiltViewModel()) {
    val getProductState = viewModel.getProductState.collectAsState()
    val productData = getProductState.value.data
    val categoryProduct = productData.filter { it.productCategory == category }

    LaunchedEffect(Unit) {
        viewModel.getProducts()
    }
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding).padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = R.drawable.back,
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = category.replaceFirstChar { it.uppercase() },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fontFamily,
                    modifier = Modifier.padding(start = 20.dp),
                    color = CustomColors.primaryBlack
                )
            }

            if (categoryProduct.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No products found in this category",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = CustomColors.darkGray,
                            fontFamily = fontFamily
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categoryProduct.size) { product ->
                        SearchResultItem(
                            product = categoryProduct[product],
                            userId = userId,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}