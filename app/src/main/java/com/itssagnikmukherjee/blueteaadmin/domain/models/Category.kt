package com.itssagnikmukherjee.blueteaadmin.domain.models

data class Category(
    val categoryName: String = "",
    val date: Long = System.currentTimeMillis(),
    val imageUrl: String = "",
)