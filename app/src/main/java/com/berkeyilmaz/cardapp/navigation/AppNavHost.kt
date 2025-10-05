package com.berkeyilmaz.cardapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object SignIn : Screen("sign_in", "Sign In")
    object SignUp : Screen("sign_up", "Sign Up")
    object Scan : Screen("scan", "Scan", Icons.Filled.CameraAlt)
    object Contacts : Screen("contacts", "Contacts", Icons.Filled.Contacts)
    object Profile : Screen("profile", "Profile", Icons.Filled.Person)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(), modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.SignIn.route) {
        composable(Screen.SignIn.route) {
//            SignInScreen(navController)
        }
        composable(Screen.SignUp.route) {
//            SignUpScreen(navController)
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