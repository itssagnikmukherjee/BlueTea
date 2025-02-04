package com.itssagnikmukherjee.blueteauser.domain.models

data class Product(
    val productId : String = "",
    val productName : String = "",
    val productDescription : String="",
    val productPrePrice : Int = 0,
    val productFinalPrice : Int = 0,
    val productCategory : String = "",
    val productImages : List<String> = emptyList(),
    val dateAdded : Long = System.currentTimeMillis(),
    val availableUnits : Int = 0,
    val isAvailable : Boolean = availableUnits>0
)
