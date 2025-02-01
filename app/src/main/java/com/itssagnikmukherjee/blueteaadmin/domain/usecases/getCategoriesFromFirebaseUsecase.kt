package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import javax.inject.Inject

class getCategoriesFromFirebaseUsecase @Inject constructor(private val repo: Repo) {

    //usecase for getting categories from firebase
    fun GetCategoriesFromFirebaseUsecase() = repo.getCategories()

}