package com.itssagnikmukherjee.blueteaadmin.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.BannerImageData
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
    private val repo: Repo,
    private val supabaseClient: SupabaseClient
) : ViewModel() {

    // Categories
    private val _addCategory = MutableStateFlow(AddCategoryState())
    val addCategoryState = _addCategory.asStateFlow()

    private suspend fun uploadImageToSupabase(
        context: Context,
        imageUri: Uri,
        categoryName: String
    ): String? {
        return try {
            // Open the input stream and read the image bytes
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes()

            // Define the folder structure and file name
            val folderPath = "categories/$categoryName"  // Folder with category name
            val fileName = "$categoryName.jpg"  // File name same as the category name

            // Upload the image to Supabase Storage under the specific folder
            val uploadResponse = supabaseClient.storage.from("categories").upload(
                path = "$folderPath/$fileName",  // Path includes folder and file name
                data = imageBytes!!
            )

            // Check if the upload was successful
            if (true) {
                // Get the public URL of the uploaded image
                return supabaseClient.storage.from("categories").publicUrl("$folderPath/$fileName")
            } else {
                throw Exception("Image upload failed: $uploadResponse")
            }
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
                repo.addBanner(banner).collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _addBanner.value = AddBannerState(isLoading = true)
                        }
                        is ResultState.Success -> {
                            _addBanner.value = AddBannerState(data = result.data, isLoading = false)
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
}

// Data Classes
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