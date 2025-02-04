package com.itssagnikmukherjee.blueteaadmin.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.common.constants.Constants
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner.BannerAnimationSettings
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner.BannerImageData
import com.itssagnikmukherjee.blueteauser.domain.usecases.getBannersFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getCategoriesFromFirebaseUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ViewModels @Inject constructor(
    private val getAllCategories: getCategoriesFromFirebaseUsecase,
    private val getAllBanners: getBannersFromFirebaseUsecase,
    private val repo: Repo,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    // Categories
    private val _addCategory = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategory.asStateFlow()

    private suspend fun uploadImageToSupabase(
        context: Context,
        imageUri: Uri,
        categoryId: String
    ): String? {
        return try {
            // Open the input stream and read the image bytes
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes()

            // Define the folder structure and file name
            val folderPath = "categories/$categoryId"  // Folder with category ID
            val fileName = "$categoryId.jpg"  // File name same as the category ID

            // Upload the image to Supabase Storage under the specific folder
            supabaseClient.storage.from("categories").upload(
                path = "$folderPath/$fileName",  // Path includes folder and file name
                data = imageBytes!!
            )

            // Get the public URL of the uploaded image
            supabaseClient.storage.from("categories").publicUrl("$folderPath/$fileName")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addCategory(category: Category, imageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _addCategory.value = AddCategoryState(isLoading = true)

            val imageUrl = if (imageUri != null) {
                uploadImageToSupabase(context, imageUri, category.categoryName)
            } else {
                null
            }

            if (imageUrl != null) {
                val updatedCategory = category.copy(imageUrl = imageUrl)
                repo.addCategory(updatedCategory).collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _addCategory.value = AddCategoryState(isLoading = true)
                        }

                        is ResultState.Success -> {
                            _addCategory.value =
                                AddCategoryState(data = result.data, isLoading = false)
                            getCategories()
                        }

                        is ResultState.Error -> {
                            _addCategory.value =
                                AddCategoryState(error = result.error, isLoading = false)
                        }
                    }
                }
            } else {
                _addCategory.value =
                    AddCategoryState(error = "Image upload failed", isLoading = false)
            }
        }
    }
    private val _getCategoryState = MutableStateFlow(GetCategoryState())
    val getCategoryState = _getCategoryState.asStateFlow()

    fun getCategories() {
        viewModelScope.launch {
            getAllCategories.GetCategoriesFromFirebaseUsecase().collectLatest {
                when (it) {
                    is ResultState.Success -> {
                        val sortedCategories = it.data.sortedBy { category -> category.date }.reversed()
                        _getCategoryState.value = GetCategoryState(data = sortedCategories)
                    }

                    is ResultState.Error -> {
                        _getCategoryState.value = GetCategoryState(error = it.error)
                    }

                    is ResultState.Loading -> {
                        _getCategoryState.value = GetCategoryState(isLoading = true)
                    }
                }
            }
        }
    }

    private val _deleteCategory = MutableStateFlow(AddCategoryState())
    val deleteCategoryState = _deleteCategory.asStateFlow()
    fun deleteCategory(categoryName: String) {
        viewModelScope.launch {
            _deleteCategory.value = AddCategoryState(isLoading = true) // Show loading state
            repo.deleteCategory(categoryName).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _deleteCategory.value = AddCategoryState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        _deleteCategory.value = AddCategoryState(data = "Category ${categoryName} deleted successfully", isLoading = false)
                        getCategories()
                    }
                    is ResultState.Error -> {
                        _deleteCategory.value = AddCategoryState(error = result.error, isLoading = false)
                    }
                }
            }
        }
    }

    fun updateCategory(category: Category, newImageUri: Uri?, context: Context) {
        viewModelScope.launch {
            _addCategory.value = AddCategoryState(isLoading = true) // Show loading state

            try {
                // Step 1: Upload the new image to Supabase Storage (if a new image is provided)
                val imageUrl = if (newImageUri != null) {
                    uploadImageToSupabase(context, newImageUri, category.categoryName)
                } else {
                    category.imageUrl // Use the existing image URL if no new image is provided
                }

                if (imageUrl != null) {
                    // Step 2: Update the category in Firestore
                    val updatedCategory = category.copy(imageUrl = imageUrl)
                    FirebaseFirestore.getInstance()
                        .collection(Constants.CATEGORY)
                        .document(category.id) // Use the existing document ID
                        .set(updatedCategory, SetOptions.merge()) // Merge changes with existing document
                        .addOnSuccessListener {
                            _addCategory.value = AddCategoryState(data = "Category updated successfully", isLoading = false)
                            getCategories() // Refresh the list after update
                        }
                        .addOnFailureListener { e ->
                            _addCategory.value = AddCategoryState(error = "Failed to update category: ${e.message}", isLoading = false)
                        }
                } else {
                    _addCategory.value = AddCategoryState(error = "Failed to upload image", isLoading = false)
                }
            } catch (e: Exception) {
                _addCategory.value = AddCategoryState(error = "Error: ${e.message}", isLoading = false)
            }
        }
    }

    // Banner
    private val _addBanner = MutableStateFlow(AddBannerState())
    val addBannerState = _addBanner.asStateFlow()

    private suspend fun uploadBannerImagesToSupabase(
        context: Context,
        bannerImages: List<BannerImageData>
    ): List<String>? {
        return try {
            val imageUrls = mutableListOf<String>()
            for ((index, bannerImage) in bannerImages.withIndex()) {
                val imageUri = bannerImage.imageUri
                val bannerName = bannerImage.bannerName

                if (imageUri != null) {
                    val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                    val imageBytes = inputStream?.readBytes()

                    // Use the bannerName if provided, otherwise use "imageIndex.jpg"
                    val folderPath = "banners/${bannerName.takeIf { it.isNotEmpty() } ?: "image$index"}"
                    val fileName = "image.jpg" // You can customize the file name if needed

                    supabaseClient.storage.from("banners").upload(
                        path = "$folderPath/$fileName",
                        data = imageBytes!!
                    )

                    val imageUrl = supabaseClient.storage.from("banners").publicUrl("$folderPath/$fileName")
                    imageUrls.add(imageUrl)
                }
            }
            imageUrls
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addBanner(bannerImages: List<BannerImageData>, context: Context) {
        viewModelScope.launch {
            _addBanner.value = AddBannerState(isLoading = true)

            val imageUrls = uploadBannerImagesToSupabase(context, bannerImages)

            if (imageUrls != null) {
                val bannerNames = bannerImages.joinToString(", ") { it.bannerName }
                val banner = Banner(bannerNames, imageUrls)

                // Update the existing banner document
                repo.addBanner(banner).collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _addBanner.value = AddBannerState(isLoading = true)
                        }
                        is ResultState.Success -> {
                            _addBanner.value = AddBannerState(data = result.data, isLoading = false)
                            getBanners()
                        }
                        is ResultState.Error -> {
                            _addBanner.value = AddBannerState(error = result.error, isLoading = false)
                        }
                    }
                }
            } else {
                _addBanner.value = AddBannerState(error = "Image upload failed", isLoading = false)
            }
        }
    }

    private val _getBannerState = MutableStateFlow(GetBannerState())
    val getBannerState = _getBannerState.asStateFlow()

    fun getBanners() {
        viewModelScope.launch {
            getAllBanners.GetBannersFromFirebaseUsecase().collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getBannerState.value = GetBannerState(isLoading = true)
                    }
                    is ResultState.Success -> {
                        val bannerList : List<Banner> = (result.data as List<Banner>).map{it->
                            Banner(bannerImageUrls = it.bannerImageUrls, bannerName =  it.bannerName)
                        }
                        _getBannerState.value = GetBannerState(data= bannerList)
                    }
                    is ResultState.Error -> {
                        _getBannerState.value = GetBannerState(error = result.error)
                    }
                }
            }
        }
    }

    fun saveBannerSettings(settings: BannerAnimationSettings) {
        val db = FirebaseFirestore.getInstance()
        db.collection("BANNER_SETTINGS").document("settings")
            .set(settings)
            .addOnSuccessListener {
                Log.d("Admin", "Settings updated successfully")
                getBanners()
            }
            .addOnFailureListener { e -> Log.e("Admin", "Error updating settings", e) }
    }

    private val _bannerSettingsState = mutableStateOf<BannerAnimationSettings?>(null)
    val bannerSettingsState = _bannerSettingsState

    fun fetchBannerSettings(onSettingsLoaded: (BannerAnimationSettings) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("BANNER_SETTINGS").document("settings")
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val settings = document.toObject(BannerAnimationSettings::class.java)
                    settings?.let { onSettingsLoaded(it) }
                }
            }
            .addOnFailureListener { e -> Log.e("User", "Error fetching settings", e) }
    }


    //Product
    private val _addProduct = MutableStateFlow(AddProductState())
    val addProductState = _addProduct.asStateFlow()
    fun addProduct(product: Product, context: Context, imageUris: List<Uri>) {
        viewModelScope.launch {
            try {
                // Step 1: Upload images to Supabase and get their URLs
                val imageUrls = uploadProductImagesToSupabase(context, imageUris, product.productName)

                // Step 2: Create the product with the image URLs
                val updatedProduct = product.copy(productImages = imageUrls)

                // Step 3: Add the product to the database
                repo.addProduct(updatedProduct).collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _addProduct.value = AddProductState(isLoading = true)
                        }
                        is ResultState.Success -> {
                            _addProduct.value = AddProductState(data = result.data, isLoading = false)
                        }
                        is ResultState.Error -> {
                            _addProduct.value = AddProductState(error = result.error, isLoading = false)
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle image upload errors
                _addProduct.value = AddProductState(error = e.message ?: "Failed to upload images", isLoading = false)
            }
        }
    }

    private suspend fun uploadProductImagesToSupabase(
        context: Context,
        imageUris: List<Uri>,
        productId: String
    ): List<String> {
        val imageUrls = mutableListOf<String>()

        try {
            for ((index, uri) in imageUris.withIndex()) {
                // Open the input stream and read the image bytes
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val imageBytes = inputStream?.readBytes()

                // Define the folder structure and file name
                val folderPath = "products/$productId"
                val fileName = "image_${index + 1}.jpg" // Unique file name for each image

                // Upload the image to Supabase Storage under the specific folder
                supabaseClient.storage.from("products").upload(
                    path = "$folderPath/$fileName",  // Path includes folder and file name
                    data = imageBytes!!
                )

                // Get the public URL of the uploaded image
                val imageUrl = supabaseClient.storage.from("products").publicUrl("$folderPath/$fileName")
                imageUrls.add(imageUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e // Rethrow the exception to handle it in the calling function
        }

        return imageUrls
    }

}

// Data Classes
data class AddProductState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String = ""
)

data class AddBannerState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String = ""
)

data class AddCategoryState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String = ""
)

data class GetCategoryState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: List<Category?> = emptyList()
)

data class GetBannerState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<Banner> = emptyList()
)