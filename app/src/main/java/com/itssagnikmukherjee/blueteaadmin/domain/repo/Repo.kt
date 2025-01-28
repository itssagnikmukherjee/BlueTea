package com.itssagnikmukherjee.blueteaadmin.domain.repo

import com.itssagnikmukherjee.blueteaadmin.common.ResultState
import com.itssagnikmukherjee.blueteaadmin.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface Repo {
    fun addCategory(category: Category):Flow<ResultState<String>>
}