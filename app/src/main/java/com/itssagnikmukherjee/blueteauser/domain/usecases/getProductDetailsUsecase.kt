package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import javax.inject.Inject

class getProductDetailsUsecase @Inject constructor(private val repo: Repo){
    fun GetProductDetailsUsecase(productId: String) = repo.getProductDetailsById(productId)
}