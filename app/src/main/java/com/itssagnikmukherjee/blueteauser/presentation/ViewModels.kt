package com.itssagnikmukherjee.blueteauser.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import com.itssagnikmukherjee.blueteauser.domain.usecases.getBannersFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getCategoriesFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.getProductsFromFirebaseUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.loginUserWithEmailAndPassUsecase
import com.itssagnikmukherjee.blueteauser.domain.usecases.registerUserWithEmailUsecase
import com.itssagnikmukherjee.blueteauser.presentation.screens.BannerAnimationSettings
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val getAllProducts: getProductsFromFirebaseUsecase,
    private val registerUserWithEmail: registerUserWithEmailUsecase,
    private val loginUserWithEmail : loginUserWithEmailAndPassUsecase
) : ViewModel() {

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
                        val bannerList : List<Banner> = (result.data as List<Banner>).map{it->
                            Banner(it.bannerImageUrls, it.bannerName)
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

//  Register user
    private val _registerUserState = MutableStateFlow(RegisterUserState())
    val registerUserState = _registerUserState.asStateFlow()

    fun registerUserWithEmail(userData: UserData) {
        viewModelScope.launch{
            registerUserWithEmail.RegisterUserWithEmailAndPass(userData).collectLatest{result->
                when(result){
                    is ResultState.Loading ->{
                        _registerUserState.value = RegisterUserState(isLoading = true)
                    }
                    is ResultState.Success ->{
                        _registerUserState.value = RegisterUserState(data = result.data)
                    }
                    is ResultState.Error ->{
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
}

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