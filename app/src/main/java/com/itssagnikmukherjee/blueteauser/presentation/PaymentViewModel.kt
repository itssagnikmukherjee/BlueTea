package com.itssagnikmukherjee.blueteauser.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itssagnikmukherjee.blueteauser.common.utils.StripeApiInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val stripeApi: StripeApiInterface
) : ViewModel() {

    private val _ephemeralKey = MutableLiveData<String?>()
    val ephemeralKey: LiveData<String?> get() = _ephemeralKey

    fun fetchEphemeralKey(customerId: String) {
        viewModelScope.launch {
            try {
                val response = stripeApi.getEphemeralKey("2022-11-15", customerId)
                if (response.isSuccessful) {
                    _ephemeralKey.postValue(response.body()?.id)
                } else {
                    Log.e("Stripe", "Failed to fetch ephemeral key: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("Stripe", "Error fetching ephemeral key", e)
            }
        }
    }
}

