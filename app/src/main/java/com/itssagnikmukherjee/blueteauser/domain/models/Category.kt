package com.itssagnikmukherjee.blueteauser.domain.models

data class Category(
    val categoryName: String = "",
    val date: Long = System.currentTimeMillis(),
    val imageUrl: String = "",
)