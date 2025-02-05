package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import javax.inject.Inject

class registerUserWithEmailUsecase @Inject constructor(private val repo: Repo){
    fun RegisterUserWithEmailAndPass(userData: UserData) = repo.registerUserWithEmailAndPass(userData)
}