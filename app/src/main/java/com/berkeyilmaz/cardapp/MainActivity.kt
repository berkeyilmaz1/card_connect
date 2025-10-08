package com.berkeyilmaz.cardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.berkeyilmaz.cardapp.core.navigation.AppNavHost
import com.berkeyilmaz.cardapp.presentation.ui.theme.CardAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CardAppTheme {
                AppNavHost()
            }
        }
    }
}
