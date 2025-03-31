package com.itssagnikmukherjee.blueteauser.domain.models

import android.media.Rating
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

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
    val isAvailable : Boolean = availableUnits>0,
    val randomRating: Double = (40..50).random().toDouble(),
    val randomUserRated : Int = (1000..5000).random()
)