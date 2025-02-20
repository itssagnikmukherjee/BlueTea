package com.itssagnikmukherjee.blueteauser.common.utils

import com.google.gson.annotations.SerializedName
import com.itssagnikmukherjee.blueteauser.BuildConfig
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface StripeApiInterface {
    @FormUrlEncoded
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Header("Stripe-Version") stripeVersion: String,
        @Field("customer") customerId: String
    ): Response<CustomerPaymentDetails>
}
data class CustomerPaymentDetails(val id: String)