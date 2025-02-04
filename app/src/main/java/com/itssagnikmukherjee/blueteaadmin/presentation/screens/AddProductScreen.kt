package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels

@Composable
fun AddProductScreen(modifier: Modifier = Modifier, viewModel: ViewModels = hiltViewModel()) {
    var productName by remember { mutableStateOf("") }
    var productDescription by remember { mutableStateOf("") }
    var prePrice by remember { mutableIntStateOf(0) }
    var finalPrice by remember { mutableIntStateOf(0) }
    var productCategory by remember { mutableStateOf("") }
    var productImageUris by remember { mutableStateOf<List<Uri?>>(emptyList()) }
    var availableUnits by remember { mutableIntStateOf(0) }
    val productState by viewModel.addProductState.collectAsState()
    val isLoading = productState.isLoading
    val getCategoryState by viewModel.getCategoryState.collectAsState()
    val context = LocalContext.current

    var isDropdownExpanded by remember { mutableStateOf(false) } // State for dropdown visibility

    // Fetch categories when the screen is launched
    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let{selectedUri->
            if(productImageUris.size<5){
                productImageUris += selectedUri
            }else{
                Log.d("Admin", "Maximum 5 images allowed")
            }
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Add Product")

        Spacer(modifier = Modifier.height(16.dp))

        // Product Image Picker
        LazyRow {
            items(productImageUris.size) { index ->
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(Color.Gray, RoundedCornerShape(10.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(productImageUris[index]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Image",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clickable {
                                productImageUris = productImageUris.toMutableList().apply{removeAt(index)}
                            }
                    )
                }
            }
            if(productImageUris.size<5){
                item{
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .background(Color.Gray, RoundedCornerShape(10.dp))
                            .clickable { launcher.launch("image/*") }
                    ){
                        Icon(imageVector = Icons.Default.Add,"")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Product Name Field
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Product Description Field
        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Product Price Field
        OutlinedTextField(
            value = prePrice.toString(),
            onValueChange = { prePrice = it.toIntOrNull() ?: 0 },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Discount Price Field (only shown if prePrice is not zero)
        if (prePrice != 0) {
            OutlinedTextField(
                value = finalPrice.toString(),
                onValueChange = { finalPrice = it.toIntOrNull() ?: 0 },
                label = { Text("Discount Price") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Product Category Dropdown
        Box(modifier = Modifier.fillMaxWidth()
            .clickable { isDropdownExpanded = true }) {
            OutlinedTextField(
                value = productCategory,
                onValueChange = {},
                label = { Text("Product Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = true },
                readOnly = true,
                trailingIcon = { Icon(Icons.Default.ArrowDropDown,"", modifier = Modifier.padding(15.dp).clickable{ isDropdownExpanded = true }) }
            )

            if (isDropdownExpanded) {
                CategoryDropDown(
                    expanded = isDropdownExpanded,
                    categories = getCategoryState.data.mapNotNull { it?.categoryName },
                    onCategorySelected = { selectedCategory ->
                        productCategory = selectedCategory // Set the selected category
                        isDropdownExpanded = false // Close the dropdown
                    },
                    onDismiss = { isDropdownExpanded = false }// Close the dropdown on dismiss
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Available Units Field
        OutlinedTextField(
            value = availableUnits.toString(),
            onValueChange = { availableUnits = it.toIntOrNull() ?: 0 },
            label = { Text("Available Units") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Add Product Button
        Button(
            onClick = {
                val product = Product(
                    productName = productName,
                    productDescription = productDescription,
                    productPrePrice = prePrice,
                    productFinalPrice = finalPrice,
                    productCategory = productCategory,
                    productImages = emptyList(), // Initially empty, will be updated after upload
                    availableUnits = availableUnits
                )
                viewModel.addProduct(product,context, productImageUris.filterNotNull())
                // Clear the fields after adding the product
                productName = ""
                productDescription = ""
                prePrice = 0
                finalPrice = 0
                productCategory = ""
                productImageUris = emptyList()
                availableUnits = 0
                Toast.makeText(context,"Product ${product.productName} added successfully",Toast.LENGTH_LONG).show()
                Log.d("Admin", "Product added successfully")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if(isLoading){
                CircularProgressIndicator()
            }
            else{
            Text("Add Product")
            }
        }
    }
}

@Composable
fun CategoryDropDown(
    expanded: Boolean,
    categories: List<String>,
    onCategorySelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { onDismiss() },
        modifier = Modifier.padding(5.dp)
    ) {
        categories.forEach { category ->
            DropdownMenuItem(
                text = { Text(text = category) },
                onClick = {
                    onCategorySelected(category)
                    onDismiss()
                },
                modifier = Modifier.width(150.dp)
            )
        }
    }
}
