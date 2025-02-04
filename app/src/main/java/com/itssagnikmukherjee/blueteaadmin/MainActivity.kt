package com.itssagnikmukherjee.blueteaadmin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.AddCategoryScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.screens.banner.AddBannerScreen
import com.itssagnikmukherjee.blueteaadmin.presentation.theme.BlueTeaAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlueTeaAdminTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    AddCategoryScreen()
                    AddBannerScreen()
                }
            }
        }
    }
}
