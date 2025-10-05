package com.berkeyilmaz.cardapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.widgets.AppBottomBar
import com.berkeyilmaz.cardapp.navigation.AppNavHost
import com.berkeyilmaz.cardapp.navigation.Screen

@Composable
fun MainView() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(Screen.Scan.route, Screen.Contacts.route, Screen.Profile.route)

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                AppBottomBar(navController)
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
