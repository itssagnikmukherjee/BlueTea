package com.itssagnikmukherjee.blueteauser.domain.repo

import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.domain.models.Banner
import com.itssagnikmukherjee.blueteauser.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun getCategories(): Flow<ResultState<List<Category>>>
    fun getBanners(): Flow<ResultState<List<Banner>>>
}