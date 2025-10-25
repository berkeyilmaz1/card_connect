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
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.navigation.AppNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.presentation.settings.viewmodel.SettingsViewModel
import com.berkeyilmaz.cardapp.presentation.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            LaunchedEffect(firebaseUser) {
                if (firebaseUser != null) {
                    navController.navigate(Screen.MainView.route) {
                        popUpTo(Screen.AuthGraph.route) {
                            inclusive = true
                        }
                    }
                } else {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.AuthGraph.route) {
                            inclusive = true
                        }
                    }
                }
            }
            val viewModel: SettingsViewModel = hiltViewModel()
            val currentTheme by viewModel.currentTheme.collectAsState()

            AppTheme(theme = currentTheme) {
                AppNavHost(navController)
            }
        }
    }
}
