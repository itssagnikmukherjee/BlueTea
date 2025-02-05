package com.itssagnikmukherjee.blueteauser.domain.usecases

import com.itssagnikmukherjee.blueteauser.domain.repo.Repo
import javax.inject.Inject

class loginUserWithEmailAndPassUsecase @Inject constructor(
    private val repo: Repo
) {
    fun LoginUserWithEmailAndPassUsecase(email: String, pass: String) = repo.loginUserWithEmailAndPass(email,pass)
}