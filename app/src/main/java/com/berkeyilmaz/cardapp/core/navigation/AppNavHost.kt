package com.berkeyilmaz.cardapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.utility.navigateAndClearBackStack
import com.berkeyilmaz.cardapp.core.utility.safeNavigate
import com.berkeyilmaz.cardapp.core.utility.safePopBack
import com.berkeyilmaz.cardapp.presentation.auth.forgot_password.ForgotPasswordView
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.main.MainView
import com.berkeyilmaz.cardapp.presentation.main.MainViewModel


@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.Graph.route
) {
    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        // ========== AUTH GRAPH ==========
        navigation(
            route = Screen.Auth.Graph.route, startDestination = Screen.Auth.SignIn.route
        ) {
            composable(Screen.Auth.SignIn.route) {
                LaunchedEffect(Unit) { mainViewModel.analyticsManager.logScreenView("SignIn") }
                SignInView(onNavigateToMain = {
                    navController.navigateAndClearBackStack(
                        route = Screen.Main.Graph.route,
                        upToRoute = Screen.Auth.Graph.route,
                        inclusive = true
                    )
                }, onNavigateForgotPassword = {
                    navController.safeNavigate(Screen.Auth.ForgotPassword.route)
                })
            }

            composable(Screen.Auth.ForgotPassword.route) {
                LaunchedEffect(Unit) { mainViewModel.analyticsManager.logScreenView("ForgotPassword") }
                ForgotPasswordView(
                    onNavigateBack = { navController.safePopBack() })
            }
        }

        // ========== MAIN GRAPH ==========
        navigation(
            route = Screen.Main.Graph.route, startDestination = Screen.Main.Home.route
        ) {
            composable(Screen.Main.Home.route) {
                MainView(
                    rootNavController = navController
                )
            }
        }
    }
}
