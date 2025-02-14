package com.itssagnikmukherjee.blueteauser.domain.models

data class UserData (
    val firstName: String="",
    val lastName: String="",
    val email: String="",
    val address: String="",
    val userImage : String="",
    val password: String="",
    val phoneNo: String="",
    var userId : String ="",
    val cartItems : List<String> = emptyList(),
    val wishlistItems : List<String> = emptyList()
)