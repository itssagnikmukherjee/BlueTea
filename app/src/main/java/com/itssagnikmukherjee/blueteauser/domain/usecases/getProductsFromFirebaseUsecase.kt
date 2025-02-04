package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import javax.inject.Inject

class getProductsFromFirebaseUsecase @Inject constructor(private val repo: Repo) {
    fun GetProductsFromFirebaseUsecase() = repo.getProducts()
}