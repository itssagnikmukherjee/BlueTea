package com.itssagnikmukherjee.blueteaadmin.domain.repo

import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.domain.models.Banner
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface Repo {

    fun addCategory(category: Category):Flow<ResultState<String>>
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun deleteCategory(categoryId: String): Flow<ResultState<String>>

    fun addBanner(banner: Banner):Flow<ResultState<String>>
    fun getBanners(): Flow<ResultState<List<Banner>>>
    fun deleteBanner(bannerId: String): Flow<ResultState<String>>

}