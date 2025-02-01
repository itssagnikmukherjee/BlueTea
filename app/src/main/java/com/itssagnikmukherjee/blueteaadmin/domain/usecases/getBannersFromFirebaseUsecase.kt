package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import javax.inject.Inject

class getBannersFromFirebaseUsecase @Inject constructor(private val repo: Repo) {

    //usecase for getting categories from firebase
    fun GetBannersFromFirebaseUsecase() = repo.getBanners()

}