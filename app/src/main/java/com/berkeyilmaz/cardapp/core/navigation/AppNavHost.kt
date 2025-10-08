package com.berkeyilmaz.cardapp.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.berkeyilmaz.cardapp.core.constants.Constants
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.auth.signup.SignUpView
import com.berkeyilmaz.cardapp.presentation.scan.ScanView


sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object SignIn : Screen("sign_in", Constants.APP_NAME)
    object SignUp : Screen("sign_up", Constants.APP_NAME)
    object Scan : Screen("scan", "Scan", Icons.Filled.CameraAlt)
    object Contacts : Screen("contacts", "Contacts", Icons.Filled.Contacts)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.SignIn.route) {
        composable(Screen.SignIn.route) {
            SignInView(onNavigate = { route, email ->
                navController.navigate(route + "?email=" + email) {
                    popUpTo(Screen.SignIn.route) {
                        inclusive = true
                    }
                }
            })
        }
        composable(Screen.SignUp.route) {
            SignUpView(
                onNavigate = { route, email ->
                    navController.navigate(Screen.Scan.route + "?email=$email") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }
        composable(
            Screen.Scan.route + "?email={email}",
            arguments = listOf(
                navArgument("email") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ScanView(email = email)
        }
        composable(Screen.Contacts.route) {
//            ContactsScreen(navController)
        }
        composable(Screen.Profile.route) {
//            ProfileScreen(navController)
        }
        composable(Screen.Settings.route) {
//            SettingsScreen(navController)
        }
    }
}