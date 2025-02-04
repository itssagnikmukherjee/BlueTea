package com.itssagnikmukherjee.blueteaadmin.domain.models

data class Product(
    val productName : String,
    val productDescription : String,
    val productPrePrice : Int,
    val productFinalPrice : Int,
    val productCategory : String,
    val productImage : String,
    val dateAdded : Long = System.currentTimeMillis(),
    val availableUnits : Int,
    val isAvailable : Boolean = availableUnits>0
)