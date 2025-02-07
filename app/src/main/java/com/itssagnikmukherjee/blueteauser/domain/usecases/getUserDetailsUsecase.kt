package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import javax.inject.Inject

class getUserDetailsUsecase @Inject constructor(private val repo: Repo){
    fun GetUserDetailsUsecase(userData: UserData) = repo.getUserDetails(userData.userId)
}