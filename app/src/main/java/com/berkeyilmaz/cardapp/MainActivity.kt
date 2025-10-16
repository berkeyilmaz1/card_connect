package com.berkeyilmaz.cardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.navigation.AppNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.presentation.ui.theme.CardAppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        splashScreen.setKeepOnScreenCondition {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            firebaseUser == null
        }
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
            CardAppTheme {
                AppNavHost(navController)
            }
        }
    }
}
