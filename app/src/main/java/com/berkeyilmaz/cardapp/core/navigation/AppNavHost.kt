package com.berkeyilmaz.cardapp.core.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.presentation.auth.forgot_password.ForgotPasswordView
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.main.main.MainView

sealed interface Screen {
    val route: String
    @get:StringRes
    val titleRes: Int?

    // Auth Screens
    sealed interface Auth : Screen {
        data object Graph : Auth {
            override val route = "auth_graph"
            override val titleRes = null
        }

        data object SignIn : Auth {
            override val route = "sign_in"
            override val titleRes = R.string.sign_in
        }

        data object ForgotPassword : Auth {
            override val route = "forgot_password"
            override val titleRes = R.string.forgot_password
        }
    }

    // Main Screens
    sealed interface Main : Screen {
        data object Graph : Main {
            override val route = "main_graph"
            override val titleRes = null
        }

        // Bottom Navigation Screens
        data object Home : Main {
            override val route = "home"
            override val titleRes = R.string.home
        }

        data object Contact : Main {
            override val route = "contacts"
            override val titleRes = R.string.contacts
        }

        data object More : Main {
            override val route = "more"
            override val titleRes = R.string.more
        }

        // Full Screen Screens ()
        data object Settings : Main {
            override val route = "settings"
            override val titleRes = R.string.settings
        }

        data object Profile : Main {
            override val route = "profile"
            override val titleRes = R.string.profile
        }

        data object Scan : Main {
            override val route = "scan"
            override val titleRes = R.string.scan
        }

        // Parameterized Routes
        data class ContactDetail(val id: String) : Main {
            override val route = "contact_detail/$id"
            override val titleRes = R.string.contact_detail

            companion object {
                const val ROUTE_PATTERN = "contact_detail/{id}"
                fun createRoute(id: String) = "contact_detail/$id"
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.Graph.route
) {
    NavHost(
        navController = navController, startDestination = startDestination
    ) {
        // ========== AUTH GRAPH ==========
        navigation(
            route = Screen.Auth.Graph.route, startDestination = Screen.Auth.SignIn.route
        ) {
            composable(Screen.Auth.SignIn.route) {
                SignInView(onNavigateToMain = {

                    navController.navigate(Screen.Main.Graph.route) {
                        popUpTo(Screen.Auth.Graph.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }, onNavigateForgotPassword = {
                    navController.navigate(Screen.Auth.ForgotPassword.route)
                })
            }

            composable(Screen.Auth.ForgotPassword.route) {
                ForgotPasswordView(
                    onNavigateBack = { navController.navigateUp() })
            }
        }

        // ========== MAIN GRAPH ==========
        navigation(
            route = Screen.Main.Graph.route, startDestination = Screen.Main.Home.route
        ) {
            composable(Screen.Main.Home.route) {
                MainView(
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.Graph.route) {
                            popUpTo(Screen.Main.Graph.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
