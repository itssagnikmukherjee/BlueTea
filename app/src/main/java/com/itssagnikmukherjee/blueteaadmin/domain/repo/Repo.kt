package com.itssagnikmukherjee.blueteaadmin.domain.repo

import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.data.repoImpl
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import com.itssagnikmukherjee.blueteaadmin.domain.models.OrderDetails
import com.itssagnikmukherjee.blueteaadmin.domain.models.Product
import com.itssagnikmukherjee.blueteaadmin.domain.models.UserData
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun addCategory(category: Category):Flow<ResultState<String>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun deleteCategory(categoryId: String): Flow<ResultState<String>>
    fun addBanner(banner: Banner):Flow<ResultState<String>>
    fun getBanners(): Flow<ResultState<List<Banner>>>
    fun addProduct(product: Product): Flow<ResultState<String>>
    fun getProducts(): Flow<ResultState<List<Product>>>
    fun getUserDetails(userId: String): Flow<ResultState<UserData>>
    fun getOrders(): Flow<ResultState<List<OrderDetails>>>
    fun updateOrderStatus(userId: String, orderId: String, newStatus: String): Flow<ResultState<String>>
}