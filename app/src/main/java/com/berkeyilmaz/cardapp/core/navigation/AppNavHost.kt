package com.berkeyilmaz.cardapp.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.main.main.MainView

sealed class Screen(val route: String, val title: String? = null) {

    // Auth
    object AuthGraph : Screen("auth_graph")
    object SignIn : Screen("login")
    object ForgotPassword : Screen("forgot_password")

    // Main
    object MainGraph : Screen("main_graph")
    object MainView : Screen("main_view")

    // Bottom Nav Screens
    object Scan : Screen("scan_view", "Scan")
    object Contact : Screen("contact_view", "Contacts")
    object Profile : Screen("profile_view", "Profile")

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