package com.itssagnikmukherjee.blueteauser

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.common.utils.StripeApiInterface
import com.itssagnikmukherjee.blueteauser.presentation.navigation.AppNavigation
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.HomeScreenUser
import com.itssagnikmukherjee.blueteauser.presentation.screens.LoginScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.MainScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.SignUpScreen
import com.itssagnikmukherjee.blueteauser.presentation.theme.BlueTeaAdminTheme
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var stripeApi: StripeApiInterface
    private lateinit var testClientSecret: StripeApiInterface
    private lateinit var paymentSheet: PaymentSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult) // Initialize here

        setContent {
            val navController = rememberNavController()
            val userId = firebaseAuth.currentUser?.uid ?: ""

            Log.d("MainActivity", "User ID: $userId")

            BlueTeaAdminTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        navController = navController,
                        firebaseAuth = firebaseAuth,
                        userId = userId,
                        paymentSheet = paymentSheet
                    )
                }
            }
        }
    }
}

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Completed -> Log.d("Stripe", "Payment successful!")
        is PaymentSheetResult.Failed -> Log.d("Stripe", "Payment failed: ${paymentSheetResult.error}")
        is PaymentSheetResult.Canceled -> Log.d("Stripe", "Payment canceled")
    }
}