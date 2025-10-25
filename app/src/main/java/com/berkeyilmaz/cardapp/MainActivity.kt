package com.berkeyilmaz.cardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.berkeyilmaz.cardapp.core.navigation.AppNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.presentation.settings.viewmodel.SettingsViewModel
import com.berkeyilmaz.cardapp.presentation.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var keepSplashScreen = true
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: SettingsViewModel = hiltViewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()
            val firebaseUser = FirebaseAuth.getInstance().currentUser

            LaunchedEffect(Unit) {
                keepSplashScreen = false
            }

            val startDestination = if (firebaseUser != null) {
                Screen.Main.Graph.route
            } else {
                Screen.Auth.Graph.route
            }

            AppTheme(theme = currentTheme) {
                AppNavHost(startDestination = startDestination)
            }
        }
    }
}
