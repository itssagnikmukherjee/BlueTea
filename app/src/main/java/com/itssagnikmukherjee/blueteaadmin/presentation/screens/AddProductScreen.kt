package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.R
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack

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
    val getProductState by viewModel.getProductState.collectAsState()
    val isLoading = productState.isLoading
    val getCategoryState by viewModel.getCategoryState.collectAsState()
    val context = LocalContext.current

    var isDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
        viewModel.getProducts()
    }

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
    if(getProductState.isLoading){ShimmerScreen()} else
    Column(modifier = modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        AllProducts()
        Spacer(Modifier.height(20.dp))
        Box(
            Modifier
                .width(150.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Add Products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart),
                color = primaryBlack
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ){
            items(productImageUris.size) { index ->
                Box(
                    modifier = Modifier
                        .size(200.dp).fillMaxWidth()
                        .clickable { launcher.launch("image/*") }.background(Color.Gray, RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(productImageUris[index]),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = {
                        productImageUris = productImageUris.toMutableList().apply{removeAt(index)}
                    }, modifier = Modifier.align(Alignment.TopEnd)
                        .padding(8.dp).size(30.dp), colors = IconButtonDefaults.iconButtonColors(
                            containerColor = primaryBlack,
                            contentColor = Color.White
                        )) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete Image",
                            tint = Color.White
                        )
                    }
                }
            }
            if(productImageUris.size<5){
                item{
                    Box(
                        modifier = Modifier
                            .size(200.dp).fillMaxWidth()
                            .background(Color.LightGray, RoundedCornerShape(20.dp))
                            .clickable { launcher.launch("image/*") }
                    ){
                        Icon(painter = painterResource(R.drawable.image_solid),"", tint = primaryBlack, modifier = Modifier.align(
                            Alignment.Center).size(30.dp))
                    }
                }
            }
        }

        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = primaryBlack,
            unfocusedBorderColor = primaryBlack,
            cursorColor = primaryBlack,
            focusedLabelColor = primaryBlack,
            unfocusedLabelColor = primaryBlack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = textFieldColors,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = productDescription,
            onValueChange = { productDescription = it },
            label = { Text("Product Description") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = prePrice.toString(),
            onValueChange = { prePrice = it.toIntOrNull() ?: 0 },
            label = { Text("Price") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (prePrice != 0) {
            OutlinedTextField(
                value = finalPrice.toString(),
                onValueChange = { finalPrice = it.toIntOrNull() ?: 0 },
                label = { Text("Discount Price") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(15.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()
            .clickable { isDropdownExpanded = true }) {
            OutlinedTextField(
                value = productCategory,
                colors = textFieldColors,
                shape = RoundedCornerShape(15.dp),
                onValueChange = {},
                label = { Text("Product Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDropdownExpanded = true },
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown,"", modifier = Modifier.padding(20.dp).clickable{ isDropdownExpanded = true }.clip(CircleShape))
                }
            )

            if (isDropdownExpanded) {
                CategoryDropDown(
                    expanded = isDropdownExpanded,
                    categories = getCategoryState.data.mapNotNull { it?.categoryName },
                    onCategorySelected = { selectedCategory ->
                        productCategory = selectedCategory
                        isDropdownExpanded = false
                    },
                    onDismiss = { isDropdownExpanded = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = availableUnits.toString(),
            onValueChange = { availableUnits = it.toIntOrNull() ?: 0 },
            label = { Text("Available Units") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            shape = RoundedCornerShape(15.dp)
        )

        val isFormValid = productName.isNotBlank() &&
                productDescription.isNotBlank() &&
                prePrice > 0 &&
                finalPrice >= 0 &&
                productCategory.isNotBlank() &&
                availableUnits > 0 &&
                productImageUris.isNotEmpty()

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val product = Product(
                    productName = productName,
                    productDescription = productDescription,
                    productPrePrice = prePrice,
                    productFinalPrice = finalPrice,
                    productCategory = productCategory,
                    productImages = emptyList(),
                    availableUnits = availableUnits
                )
                viewModel.addProduct(product, context, productImageUris.filterNotNull())
                productName = ""
                productDescription = ""
                prePrice = 0
                finalPrice = 0
                productCategory = ""
                productImageUris = emptyList()
                availableUnits = 0
                Toast.makeText(context, "Product ${product.productName} added successfully", Toast.LENGTH_LONG).show()
                Log.d("Admin", "Product added successfully")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) primaryBlack else Color.Gray,
                contentColor = Color.White
            ),
            enabled = isFormValid
        ) {
            Row {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(10.dp))
                Text("Add Product", fontFamily = fontFamily)
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
            modifier = Modifier.padding(5.dp),
            shadowElevation = 1.dp,
            shape = RoundedCornerShape(20.dp),
            offset = DpOffset(220.dp, 0.dp),
            tonalElevation = 10.dp
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category, textAlign = TextAlign.Center ,color = primaryBlack, fontFamily = fontFamily, modifier = Modifier.fillMaxWidth()) },
                    onClick = {
                        onCategorySelected(category)
                        onDismiss()
                    },
                    modifier = Modifier.width(150.dp)
                )
            }
        }
}

@Composable
fun AllProducts(viewModel: ViewModels = hiltViewModel()){
    val getProductState = viewModel.getProductState.collectAsState()
    val productData = getProductState.value.data
    var isProductCardExpanded by remember { mutableStateOf(false) }

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            Modifier
                .width(115.dp)
                .height(50.dp)
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .fillMaxWidth()
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    "${productData.size}",
                    fontSize = 14.sp,
                    color = Color.White,
                    modifier = Modifier
                        .background(primaryBlack)
                        .fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = "Products",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterStart),
                color = primaryBlack
            )
        }

        IconButton(onClick = {
            viewModel.getProducts()
        }) {
            Icon(painter = painterResource(R.drawable.arrows_rotate_solid), contentDescription = null, tint = primaryBlack, modifier = Modifier.size(20.dp))
        }
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(productData.size) {
            Card(
                modifier = Modifier.width(200.dp).clip(RoundedCornerShape(20.dp))
            ){
                Box{
                AsyncImage(model = productData[it].productImages[0], contentDescription = null, modifier = Modifier.size(200.dp).clip(RoundedCornerShape(20.dp)))
                    IconButton(onClick = {
                        isProductCardExpanded = !isProductCardExpanded
                    }, modifier = Modifier.padding(10.dp).align(Alignment.TopEnd).size(24.dp)
                        , colors = IconButtonDefaults.iconButtonColors(
                        containerColor = primaryBlack,
                            contentColor = Color.White
                    ),
                        ) {
                        Icon(painter = painterResource(R.drawable.ellipsis_solid), contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
                AnimatedVisibility(visible = isProductCardExpanded) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Row(
                            Modifier.fillMaxWidth().height(30.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                    Text(text = productData[it].productName, color = primaryBlack)
                            IconButton(onClick = {}, modifier = Modifier.size(28.dp), colors = IconButtonDefaults.iconButtonColors(containerColor = primaryBlack)) {
                                Icon(painter = painterResource(R.drawable.change),"", modifier = Modifier.size(14.dp), tint = Color.White)
                            }
                        }
                        Row {
                            Box(Modifier.border(1.dp, primaryBlack, RoundedCornerShape(10.dp))) {
                                Text(
                                    text = productData[it].productCategory,
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 2.dp
                                    ),
                                    color = primaryBlack,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Box(Modifier.clip(CircleShape).background(primaryBlack)) {
                                Text(
                                    text = productData[it].availableUnits.toString(),
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 2.dp
                                    ),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(Modifier.width(5.dp))
                            Text("₹${productData[it].productFinalPrice}", color = primaryBlack)
                        }
                    }
                }
            }
        }
    }
}