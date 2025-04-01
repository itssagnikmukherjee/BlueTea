package com.itssagnikmukherjee.blueteaadmin.presentation.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteaadmin.R
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.presentation.ViewModels
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.fontFamily
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.primaryBlack
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
    val deleteCategoryState by viewModel.deleteCategoryState.collectAsState()
    val getCategoriesState by viewModel.getCategoryState.collectAsState()
    val updateCategoryState by viewModel.updateCategoryState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> categoryImageUri = uri }

    LaunchedEffect(Unit) {
        viewModel.getCategories()
    }

    LaunchedEffect(categoryState.data) {
        if (categoryState.data.isNotEmpty()) {
            Toast.makeText(context, "Category $categoryName added successfully!", Toast.LENGTH_SHORT).show()
            categoryName = ""
            categoryImageUri = null
        }
    }

    LaunchedEffect(updateCategoryState.data) {
        if (updateCategoryState.data.isNotEmpty()) {
            Toast.makeText(context, updateCategoryState.data, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(deleteCategoryState.data) {
        if (deleteCategoryState.data.isNotEmpty()) {
            Toast.makeText(context, deleteCategoryState.data, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        Modifier.padding(vertical = 10.dp)
    ) {
        var isRefreshing by remember { mutableStateOf(false) }

        val refreshRotation by animateFloatAsState(
            targetValue = if (isRefreshing) 0f else 360f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            label = "refreshRotation"
        )

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                Modifier.width(140.dp).height(50.dp)
            ) {
                Box(
                    Modifier.size(22.dp).clip(CircleShape).fillMaxWidth().align(Alignment.TopEnd)
                ) {
                    Text(
                        "${getCategoriesState.data.size}",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.background(primaryBlack).fillMaxSize(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal
                    )
                }
                Text(
                    text = "Categories",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterStart),
                    color = primaryBlack
                )
            }
            IconButton(onClick = {
                isRefreshing = true
            }) {
                Icon(
                    painter = painterResource(R.drawable.arrows_rotate_solid),
                    contentDescription = "Refresh",
                    modifier = Modifier
                        .rotate(refreshRotation)
                        .size(18.dp),
                    tint = primaryBlack
                )
            }
        }

        if (categoryState.isLoading || getCategoriesState.isLoading) {
            ShimmerScreen()
        } else {
            Column(
                Modifier.fillMaxWidth()
            ){
                EditCategoryItems(getCategoriesState.data, viewModel, context)
                Text(
                    text = "Add New",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp),
                    color = primaryBlack
                )
                Column(
                    modifier = Modifier
                        .padding(16.dp).fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(Modifier.clip(CircleShape)) {
                        Box(
                            modifier = Modifier
                                .size(200.dp).border(4.dp, Color.LightGray, CircleShape)
                                .background(Color.LightGray, CircleShape)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (categoryImageUri != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(categoryImageUri),
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    painter = painterResource(R.drawable.image_solid),
                                    "",
                                    modifier = Modifier.size(30.dp),
                                    tint = primaryBlack
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = {
                            categoryName = it
                        },
                        trailingIcon = {
                            if(categoryName.isNotEmpty() && categoryImageUri != null)
                                IconButton(onClick = {
                                    val category = Category(
                                        categoryName = categoryName,
                                        date = System.currentTimeMillis()
                                    )
                                    viewModel.addCategory(category, categoryImageUri!!, context)
                                }) {
                                    Icon(painter = painterResource(R.drawable.circle_check_solid), contentDescription = "Add Category" ,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                       },
                        placeholder = { Text("Category Name", fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = CustomColors.mediumGray) },
                        modifier = Modifier.width(200.dp).padding(vertical = 15.dp),
                        singleLine = true,
                        maxLines = 1,
                        shape = RoundedCornerShape(16.dp),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = primaryBlack,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Gray,
                            unfocusedIndicatorColor = Color.LightGray,
                            cursorColor = primaryBlack,
                            focusedPlaceholderColor = primaryBlack,
                            unfocusedPlaceholderColor = primaryBlack,
                        ),
                    )
                    if (categoryState.error.isNotEmpty()) {
                        Text(
                            text = "Error: ${categoryState.error}",
                            color = Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditCategoryItems(categories: List<Category?>, viewModel: ViewModels, context: Context) {
    LazyRow(
        contentPadding = PaddingValues(start = 15.dp)
    ){
        items(categories.size) { index ->
            val category = categories[index]
            if (category != null) {
                var categoryName by remember { mutableStateOf(category.categoryName) }
                var isEditing by remember { mutableStateOf(false) }
                var hasChanges by remember { mutableStateOf(false) }
                var updatedImageUri by remember { mutableStateOf<Uri?>(null) }
                var showDeleteDialog by remember { mutableStateOf(false) }
                var isMenuExpanded by remember { mutableStateOf(false) }

                val rotationAngle by animateFloatAsState(
                    targetValue = if (isMenuExpanded) 90f else 0f,
                    animationSpec = tween(durationMillis = 300),
                    label = "rotation"
                )

                val borderWidth by animateFloatAsState(
                    targetValue = if (isEditing) 3f else 2f,
                    animationSpec = tween(durationMillis = 500),
                    label = "borderWidth"
                )

                var isImagePressed by remember { mutableStateOf(false) }
                val imageScale by animateFloatAsState(
                    targetValue = if (isImagePressed && isEditing) 0.95f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "imageScale"
                )

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri: Uri? ->
                    uri?.let { selectedUri ->
                        updatedImageUri = selectedUri
                        hasChanges = true
                    }
                }

                Box(
                    Modifier.width(120.dp)
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            Modifier.height(120.dp)
                        ) {
                            Box(
                                modifier = Modifier.align(Alignment.TopEnd).zIndex(1f).align(Alignment.TopEnd)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(primaryBlack)
                                        .align(Alignment.TopEnd)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(4.dp),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .animateContentSize(
                                                    animationSpec = tween(durationMillis = 300)
                                                )
                                        ) {
                                            Row {
                                                AnimatedVisibility(
                                                    visible = isMenuExpanded,
                                                    enter = fadeIn(animationSpec = tween(durationMillis = 200)) +
                                                            expandHorizontally(
                                                                animationSpec = tween(durationMillis = 300),
                                                                expandFrom = Alignment.End
                                                            ),
                                                    exit = fadeOut(animationSpec = tween(durationMillis = 200)) +
                                                            shrinkHorizontally(
                                                                animationSpec = tween(durationMillis = 300),
                                                                shrinkTowards = Alignment.End
                                                            )
                                                ) {
                                                    Row {
                                                        if (isEditing) {
                                                            val backButtonPulse = rememberInfiniteTransition(label = "backButtonPulse")
                                                            val backButtonScale by backButtonPulse.animateFloat(
                                                                initialValue = 1f,
                                                                targetValue = 1.05f,
                                                                animationSpec = infiniteRepeatable(
                                                                    animation = tween(800),
                                                                    repeatMode = RepeatMode.Reverse
                                                                ),
                                                                label = "backButtonPulse"
                                                            )

                                                            IconButton({
                                                                isEditing = false
                                                                hasChanges = false
                                                            },
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .graphicsLayer {
                                                                        scaleX = backButtonScale
                                                                        scaleY = backButtonScale
                                                                    }
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.ArrowBack,
                                                                    contentDescription = "",
                                                                    modifier = Modifier.size(18.dp),
                                                                    tint = Color.White
                                                                )
                                                            }
                                                            if(hasChanges)
                                                            IconButton({
                                                                isEditing = false
                                                                hasChanges = false
                                                                val updatedCategory = category.copy(categoryName = categoryName)
                                                                viewModel.updateCategory(updatedCategory, updatedImageUri, context)
                                                            },
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .graphicsLayer {
                                                                        scaleX = backButtonScale
                                                                        scaleY = backButtonScale
                                                                    }
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Done,
                                                                    contentDescription = "",
                                                                    modifier = Modifier.size(18.dp),
                                                                    tint = Color.White
                                                                )
                                                            }

                                                        }


                                                        var isDeleteHovered by remember { mutableStateOf(false) }
                                                        val deleteScale by animateFloatAsState(
                                                            targetValue = if (isDeleteHovered) 1.1f else 1f,
                                                            label = "deleteScale"
                                                        )
                                                        if(!isEditing)
                                                        IconButton(
                                                            onClick = { showDeleteDialog = true },
                                                            modifier = Modifier
                                                                .size(32.dp)
                                                                .graphicsLayer {
                                                                    scaleX = deleteScale
                                                                    scaleY = deleteScale
                                                                }
                                                                .hoverable(
                                                                    interactionSource = remember { MutableInteractionSource() },
                                                                    enabled = true,
                                                                ),
                                                            colors = IconButtonDefaults.iconButtonColors(
                                                                containerColor = primaryBlack
                                                            )
                                                        ) {
                                                            Icon(
                                                                painter = painterResource(R.drawable.trash_solid),
                                                                contentDescription = "Delete Category",
                                                                modifier = Modifier
                                                                    .padding(10.dp)
                                                                    .size(22.dp),
                                                                tint = Color.White
                                                            )
                                                        }

                                                        if (!isEditing) {
                                                            var isEditHovered by remember { mutableStateOf(false) }
                                                            val editScale by animateFloatAsState(
                                                                targetValue = if (isEditHovered) 1.1f else 1f,
                                                                label = "editScale"
                                                            )

                                                            IconButton(
                                                                onClick = { isEditing = true },
                                                                modifier = Modifier
                                                                    .size(32.dp)
                                                                    .graphicsLayer {
                                                                        scaleX = editScale
                                                                        scaleY = editScale
                                                                    }
                                                                    .hoverable(
                                                                        interactionSource = remember { MutableInteractionSource() },
                                                                        enabled = true,
                                                                    )
                                                            ) {
                                                                Icon(
                                                                    painter = painterResource(R.drawable.edit),
                                                                    contentDescription = "Edit Category",
                                                                    modifier = Modifier.size(16.dp),
                                                                    tint = Color.White
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = { isMenuExpanded = !isMenuExpanded },
                                            modifier = Modifier.size(32.dp),
                                            colors = IconButtonDefaults.iconButtonColors(
                                                containerColor = primaryBlack
                                            )
                                        ) {
                                            Icon(
                                                painter = painterResource(R.drawable.ellipsis_solid),
                                                contentDescription = "Expand All options",
                                                modifier = Modifier
                                                    .padding(10.dp)
                                                    .size(32.dp)
                                                    .graphicsLayer(rotationZ = rotationAngle),
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.BottomCenter)
                                    .border(borderWidth.dp,
                                        color = if (isEditing) primaryBlack else Color.LightGray,
                                        CircleShape)
                                    .graphicsLayer {
                                        scaleX = imageScale
                                        scaleY = imageScale
                                    }
                                    .pointerInput(isEditing) {
                                        detectTapGestures(
                                            onPress = {
                                                if (isEditing) {
                                                    isImagePressed = true
                                                    try {
                                                        awaitRelease()
                                                    } finally {
                                                        isImagePressed = false
                                                    }
                                                }
                                            },
                                            onTap = {
                                                if (isEditing) {
                                                    launcher.launch("image/*")
                                                }
                                            }
                                        )
                                    }
                            ) {
                                AsyncImage(
                                    model = updatedImageUri ?: category.imageUrl,
                                    contentDescription = "Category Image",
                                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )

                                if (isEditing) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.3f))
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.image_solid),
                                            contentDescription = "Change Image",
                                            modifier = Modifier
                                                .size(20.dp)
                                                .align(Alignment.Center),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Box(
                            modifier = Modifier.height(80.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {

                            val editFieldOffset by animateFloatAsState(
                                targetValue = if (isEditing) 0f else -40f,
                                animationSpec = tween(500, easing = EaseOutQuart),
                                label = "editFieldOffset"
                            )

                            val editFieldAlpha by animateFloatAsState(
                                targetValue = if (isEditing) 1f else 0f,
                                animationSpec = tween(500),
                                label = "editFieldAlpha"
                            )

                            Column(
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationY = editFieldOffset
                                        alpha = editFieldAlpha
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(Modifier.height(10.dp))
                                OutlinedTextField(
                                    value = categoryName,
                                    onValueChange = {
                                        categoryName = it
                                        hasChanges = true
                                    },
                                    placeholder = { Text("Category", fontSize = 14.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.LightGray) },
                                    modifier = Modifier.width(120.dp),
                                    singleLine = true,
                                    maxLines = 1,
                                    shape = RoundedCornerShape(16.dp),
                                    textStyle = LocalTextStyle.current.copy(
                                        textAlign = TextAlign.Center,
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = primaryBlack,
                                        unfocusedTextColor = primaryBlack,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Gray,
                                        unfocusedIndicatorColor = primaryBlack,
                                        cursorColor = primaryBlack,
                                        focusedPlaceholderColor = primaryBlack,
                                        unfocusedPlaceholderColor = primaryBlack,
                                    ),
                                )
                            }

                            val textOffset by animateFloatAsState(
                                targetValue = if (isEditing) 40f else 0f,
                                animationSpec = tween(500, easing = EaseOutQuart),
                                label = "textOffset"
                            )

                            val textAlpha by animateFloatAsState(
                                targetValue = if (isEditing) 0f else 1f,
                                animationSpec = tween(500),
                                label = "textAlpha"
                            )

                            Text(
                                text = categoryName,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .graphicsLayer {
                                        translationY = textOffset
                                        alpha = textAlpha
                                    },
                                textAlign = TextAlign.Center,
                                color = primaryBlack,
                            )
                        }
                    }
                }
                if (showDeleteDialog) {
                    AlertDialog(
                        icon = { Icons.Default.Delete },
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Delete Category") },
                        text = { Text("Delete ${category.categoryName} ?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.deleteCategory(category.categoryName)
                                    showDeleteDialog = false
                                }
                            ) {
                                Text("Delete ${category.categoryName}")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showDeleteDialog = false }) {
                                Text("Cancel")
                            }
                        }
                    )
                }
            }
        }
    }
}