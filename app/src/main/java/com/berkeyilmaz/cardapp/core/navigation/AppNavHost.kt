package com.berkeyilmaz.cardapp.core.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.manager.TextRecognizerManager
import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.main.main.MainView
import com.berkeyilmaz.cardapp.presentation.main.main.scan.ScanView
import com.berkeyilmaz.cardapp.presentation.settings.SettingsView
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String? = null) {

    // Auth
    object AuthGraph : Screen("auth_graph")
    object SignIn : Screen("login")
    object ForgotPassword : Screen("forgot_password")

    // Main
    object MainGraph : Screen("main_graph")
    object MainView : Screen("main_view")

    // Bottom Nav Screens
    object Home : Screen("home_view", "Home")
    object Contact : Screen("contact_view", "Contacts")
    object More : Screen("more_view", "More")
    object Scan : Screen("scan_view", "Scan")
    object Settings : Screen("settings_view")
    object Profile : Screen("profile_view")


    // Fullscreen Pages
    data class ContactDetail(val id: String) : Screen("contact_detail/$id") {
        companion object {
            const val routeWithArgs = "contact_detail/{id}"
            fun createRoute(id: String) = "contact_detail/$id"
        }
    }
//
//    data class ScanDetail(val id: String) : Screen("scan_detail/$id") {
//        companion object {
//            const val routeWithArgs = "scan_detail/{id}"
//            fun createRoute(id: String) = "scan_detail/$id"
//        }
//    }
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = Screen.AuthGraph.route) {
        navigation(
            route = Screen.AuthGraph.route, startDestination = Screen.SignIn.route
        ) {
            composable(Screen.SignIn.route) {
                SignInView(
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Screen.AuthGraph.route) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateForgotPassword = { navController.navigate(Screen.ForgotPassword.route) })
            }
        }

        navigation(
            route = Screen.MainGraph.route, startDestination = Screen.MainView.route
        ) {
            // This is used for bottom navigation
            composable(Screen.MainView.route) {
                MainView()
            }

            //Fullscreen pages
//            composable(Screen.ContactDetail.routeWithArgs) { backStackEntry ->
//                val id = backStackEntry.arguments?.getString("id")
//                ContactDetailView(id)
//            }
        }
    }
}