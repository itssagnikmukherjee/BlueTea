package com.itssagnikmukherjee.blueteaadmin.domain.usecases

import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import javax.inject.Inject

class getProductsFromFirebaseUsecase @Inject constructor(private val repo: Repo) {
    fun GetProductsFromFirebaseUsecase() = repo.getProducts()
}