package com.itssagnikmukherjee.blueteauser.domain.repo

import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun registerUserWithEmailAndPass(userData: UserData): Flow<ResultState<String>>
    fun loginUserWithEmailAndPass(email: String, password: String): Flow<ResultState<String>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun getBanners(): Flow<ResultState<List<Banner>>>
    fun getProducts(): Flow<ResultState<List<Product>>>
    fun getUserDetails(userId: String): Flow<ResultState<UserData>>
    fun getProductDetailsById(productId: String): Flow<ResultState<Product>>
    fun logout()
}