package com.itssagnikmukherjee.blueteaadmin.domain.usecases

import com.itssagnikmukherjee.blueteaadmin.domain.models.UserData
import com.itssagnikmukherjee.blueteaadmin.domain.repo.Repo
import javax.inject.Inject

class getUserDetailsUsecase @Inject constructor(private val repo: Repo){
    fun GetUserDetailsUsecase(userData: UserData) = repo.getUserDetails(userData.userId)
}