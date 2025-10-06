package com.berkeyilmaz.cardapp.navigation

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
import com.berkeyilmaz.cardapp.core.constants.Constants
import com.berkeyilmaz.cardapp.presentation.auth.signin.SignInView
import com.berkeyilmaz.cardapp.presentation.auth.signup.SignUpView


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
            SignInView(onNavigateToSignUp = {
                navController.navigate(Screen.SignUp.route)
            })
        }
        composable(Screen.SignUp.route) {
            SignUpView(onNavigateToSignIn = {
                navController.popBackStack()
            })
        }
        composable(Screen.Scan.route) {
//            ScanScreen(navController)
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