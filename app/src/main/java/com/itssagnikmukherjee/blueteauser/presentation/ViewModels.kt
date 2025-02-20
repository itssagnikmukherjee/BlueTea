package com.itssagnikmukherjee.blueteauser.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.common.constants.Constants
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.domain.usecases.getBannersFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getCategoriesFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getProductDetailsUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getProductsFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getUserDetailsUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.loginUserWithEmailAndPassUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.registerUserWithEmailUsecase
import com.itssagnikmukherjee.blueteauser.presentation.screens.BannerAnimationSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class ViewModels @Inject constructor(
    private val getAllCategories: getCategoriesFromFirebaseUsecase,
    private val getAllBanners: getBannersFromFirebaseUsecase,
    private val getAllProducts: getProductsFromFirebaseUsecase,
    private val registerUserWithEmail: registerUserWithEmailUsecase,
    private val loginUserWithEmail : loginUserWithEmailAndPassUsecase,
    private val getProductDetails: getProductDetailsUsecase,
    private val supabaseClient: SupabaseClient,
    private val getUserDetails: getUserDetailsUsecase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _getProductDetailsState = MutableStateFlow(GetProductDetailsState())
    val getProductDetailsState = _getProductDetailsState.asStateFlow()

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            getProductDetails.GetProductDetailsUsecase(productId).collectLatest { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _getProductDetailsState.value = GetProductDetailsState(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _getProductDetailsState.value = GetProductDetailsState(data = result.data)
                    }

                    is ResultState.Error -> {
                        _getProductDetailsState.value = GetProductDetailsState(error = result.error)
                    }
                }
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
                        _getCategoryState.value = GetCategoryState(data = it.data)
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
                        val bannerList: List<Banner> = (result.data as List<Banner>).map { it ->
                            Banner(it.bannerImageUrls, it.bannerName)
                        }
                        _getBannerState.value = GetBannerState(data = bannerList)
                    }

                    is ResultState.Error -> {
                        _getBannerState.value = GetBannerState(error = result.error)
                    }
                }
            }
        }
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

    //products
    private val _getProductState = MutableStateFlow(GetProductState())
    val getProductState = _getProductState.asStateFlow()

    fun getProducts() {
        viewModelScope.launch {
            getAllProducts.GetProductsFromFirebaseUsecase().collectLatest { result ->
                when (result) {
                    is ResultState.Success -> {
                        // Directly assign the List<Product> to the state
                        val productList: List<Product> = result.data as List<Product>
                        _getProductState.value = GetProductState(data = productList)
                    }

                    is ResultState.Error -> {
                        _getProductState.value = GetProductState(error = result.error)
                    }

                    is ResultState.Loading -> {
                        _getProductState.value = GetProductState(isLoading = true)
                    }
                }
            }
        }
    }

    // Upload profile picture to Supabase
    private suspend fun uploadProfileImageToSupabase(
        context: Context,
        imageUri: Uri?,
        userId: String
    ): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri!!)
            val imageBytes = inputStream?.readBytes()

            val folderPath = "user-profile-pic/$userId"
            val fileName = "profile_pic_${userId}_${System.currentTimeMillis()}.jpg"


            supabaseClient.storage.from("user-profile-pic").upload(
                path = "$folderPath/$fileName",
                data = imageBytes!!
            )
            supabaseClient.storage.from("user-profile-pic").publicUrl("$folderPath/$fileName")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Register user
    private val _registerUserState = MutableStateFlow(RegisterUserState())
    val registerUserState = _registerUserState.asStateFlow()

    fun registerUserWithEmail(userData: UserData, context: Context, imageUri: Uri?) {
        viewModelScope.launch {
            _registerUserState.value = RegisterUserState(isLoading = true)

            val imageUrl = if (imageUri != null) {
                uploadProfileImageToSupabase(context, imageUri, userData.firstName)
            } else {
                null
            }

            val updatedUser = userData.copy(userImage = imageUrl ?: "")


            registerUserWithEmail.RegisterUserWithEmailAndPass(updatedUser)
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _registerUserState.value = RegisterUserState(isLoading = true)
                        }

                        is ResultState.Success -> {
                            _registerUserState.value = RegisterUserState(data = result.data)
                        }

                        is ResultState.Error -> {
                            _registerUserState.value = RegisterUserState(error = result.error)
                        }
                    }
                }
        }
    }

    //  login user
    private val _loginUserState = MutableStateFlow(LoginUserState())
    val loginUserState = _loginUserState.asStateFlow()

    fun loginWithEmailPass(email: String, pass: String) {
        viewModelScope.launch {
            loginUserWithEmail.LoginUserWithEmailAndPassUsecase(email, pass)
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _loginUserState.value = LoginUserState(isLoading = true)
                        }

                        is ResultState.Success -> {
                            _loginUserState.value = LoginUserState(data = result.data)
                        }

                        is ResultState.Error -> {
                            _loginUserState.value = LoginUserState(error = result.error)
                        }
                    }
                }
        }
    }

    //   get user details
    private val _getUserDetailsState = MutableStateFlow(GetUserDetailsState())
    val getUserDetailsState = _getUserDetailsState.asStateFlow()

    fun getUserDetails(userId: String) {
        viewModelScope.launch {
            getUserDetails.GetUserDetailsUsecase(UserData(userId = userId))
                .collectLatest { result ->
                    when (result) {
                        is ResultState.Loading -> {
                            _getUserDetailsState.value = GetUserDetailsState(isLoading = true)
                        }

                        is ResultState.Success -> {
                            _getUserDetailsState.value = GetUserDetailsState(data = result.data)
                        }

                        is ResultState.Error -> {
                            _getUserDetailsState.value = GetUserDetailsState(error = result.error)
                        }
                    }
                }
        }

    }

    // update user state
    private val _updateUserState = MutableStateFlow(GetUserDetailsState())
    val updateUserState  = _updateUserState.asStateFlow()

    fun updateUserDetails(userId: String, updatedUser: UserData, imageUri: Uri? = null, context: Context) {
        Log.d("Update User", "Updating user details: $updatedUser")
        viewModelScope.launch {
            _updateUserState.value = GetUserDetailsState(isLoading = true)
            try {
                val imageUrl = imageUri?.let { uploadProfileImageToSupabase(context, it, userId) } ?: updatedUser.userImage
                val updateMap = mapOf(
                    "firstName" to updatedUser.firstName,
                    "lastName" to updatedUser.lastName,
                    "email" to updatedUser.email,
                    "address" to updatedUser.address,
                    "userImage" to imageUrl
                )

                Log.d("UpdateUserDetails", "Updating document: users/$userId")
                Log.d("UpdateUserDetails", "Update map: $updateMap")

                val documentRef = FirebaseFirestore.getInstance().collection(Constants.USERS).document(userId)
                val documentSnapshot = documentRef.get().await()

                if (documentSnapshot.exists()) {
                    documentRef.update(updateMap).await()
                    _updateUserState.value = GetUserDetailsState(isLoading = false, data = updatedUser.copy(userImage = imageUrl))
                    Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                    getUserDetails(userId)
                } else {
                    Log.e("UpdateUserDetails", "Document does not exist: users/$userId")
                    _updateUserState.value = GetUserDetailsState(isLoading = false, error = "Document not found")
                    Toast.makeText(context, "Failed to update profile: Document not found", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("UpdateUserDetails", "Error: ${e.message}", e)
                _updateUserState.value = GetUserDetailsState(isLoading = false, error = e.message ?: "Error updating user details")
                Toast.makeText(context, "Failed to update profile!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //logout
    fun logout() {
        firebaseAuth.signOut()
    }

    //update favorite list
    fun updateFavoriteList(userId: String, productId : String, isFavorite : Boolean) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection(Constants.USERS).document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef)
                val currentWishListItems = snapshot.get("wishlistItems") as? List<String> ?: emptyList()

                val updatedWishList = if (isFavorite) {
                    currentWishListItems + productId
                } else {
                    currentWishListItems - productId
                }
                transaction.update(documentRef, "wishlistItems", updatedWishList)
            }.addOnSuccessListener {
                Log.d("UpdateFavoriteList", "Favorite list updated successfully!")
                getUserDetails(userId)
            }.addOnFailureListener { e ->
                Log.e("UpdateFavoriteList", "Error updating favorite list", e)
            }
        }
    }

    fun updateCartList(userId: String, productId : String, quantity : Int, isCarted : Boolean){
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val documentRef = db.collection(Constants.USERS).document(userId)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(documentRef)
                val currentCartItems = snapshot.get("cartItems") as? Map<String, Int> ?: emptyMap()

                val updatedCartItems = if(isCarted){
                    currentCartItems + (productId to quantity)
                }else{
                    currentCartItems - productId
                }
                transaction.update(documentRef, "cartItems", updatedCartItems)
            }.addOnSuccessListener {
                Log.d("UpdateCartList", "Cart list updated successfully!")
                getUserDetails(userId)
            }.addOnFailureListener{ e ->
                Log.e("UpdateCartList", "Error updating cart list", e)
            }
        }
    }

    fun updateCartQuantity(userId: String, productId: String, quantity: Int) {
        if (quantity > 0) {
            updateCartList(userId, productId, quantity, isCarted = true)
        } else {
            updateCartList(userId, productId, 0, isCarted = false)
        }
    }

    //place order
    fun placeOrder(userId: String, totalPrice: Double, address: String, phone: String, email: String, paymentMethod: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection(Constants.USERS).document(userId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(userRef)
                val cartItems = snapshot.get("cartItems") as? Map<String, Int> ?: emptyMap()
                val orderedItems = snapshot.get("orderedItems") as? Map<String, Any> ?: emptyMap()

                if (cartItems.isNotEmpty()) {
                    val orderId = System.currentTimeMillis().toString()

                    val orderDetails = mapOf(
                        "items" to cartItems,
                        "totalPrice" to totalPrice,
                        "address" to address,
                        "phone" to phone,
                        "email" to email,
                        "paymentMethod" to paymentMethod,
                        "timestamp" to System.currentTimeMillis(),
                        "status" to "Pending"
                    )

                    val updatedOrderedItems = orderedItems + (orderId to orderDetails)

                    // Update Firestore
                    transaction.update(userRef, "orderedItems", updatedOrderedItems)
                    transaction.update(userRef, "cartItems", emptyMap<String, Int>())
                } else {
                    throw Exception("Cart is empty!")
                }
            }.addOnSuccessListener {
                Log.d("PlaceOrder", "Order placed successfully!")
                getUserDetails(userId)
            }.addOnFailureListener { e ->
                Log.e("PlaceOrder", "Error placing order", e)
            }
        }
    }

    //cancel order
    fun cancelOrder(userId: String, orderId: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(Constants.USERS).document(userId)

        userRef.update(mapOf("orderedItems.$orderId" to FieldValue.delete()))
            .addOnSuccessListener {
                Log.d("ViewModel", "Order $orderId canceled successfully")
                getUserDetails(userId)
            }
            .addOnFailureListener { e ->
                Log.e("ViewModel", "Error canceling order", e)
            }
    }
}


data class GetUserDetailsState(
    val isLoading: Boolean = false,
    val data: UserData? = null,
    val error: String? = null
)

data class GetProductDetailsState(
    val isLoading: Boolean = false,
    val data: Product? = null,
    val error: String? = null
)

data class LoginUserState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String? = null
)

data class RegisterUserState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: String? = null
)

data class GetProductState(
    val data: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)

data class GetCategoryState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<Category?> = emptyList()
)

data class GetBannerState(
    val isLoading: Boolean = false,
    val error: String = "",
    val data: List<Banner> = emptyList()
)