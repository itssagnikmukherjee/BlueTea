package com.itssagnikmukherjee.blueteaadmin.domain.models

data class OrderDetails(
    val orderId: String = "",
    val items: Map<String, Int> = emptyMap(),
    val status: String = "",
    val timestamp: Long = 0L,
    val userId: String = "",
)