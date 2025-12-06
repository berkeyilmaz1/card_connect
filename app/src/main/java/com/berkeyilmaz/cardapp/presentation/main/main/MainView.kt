package com.berkeyilmaz.cardapp.presentation.main.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.constants.BOTTOM_TABS
import com.berkeyilmaz.cardapp.core.navigation.MainNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.core.utility.isFabVisible
import com.berkeyilmaz.cardapp.core.utility.isInRoutes
import com.berkeyilmaz.cardapp.core.utility.safeNavigate

@Composable
fun MainView() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val bottomTabsRoutes = BOTTOM_TABS.map { it.route }
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    Scaffold(
        bottomBar = {
        if (currentDestination.isInRoutes(bottomTabsRoutes)) {
            BottomBar(navController, BOTTOM_TABS)
        }
    }, floatingActionButton = {
        if (currentDestination.isFabVisible(Screen.Main.Home.route)) {
            ScanFabButton {
                navController.safeNavigate(Screen.Main.Scan.route)
            }
        }
    }, containerColor = MaterialTheme.colorScheme.background
    ) { contentPadding ->
        MainNavHost(
            navController, context, modifier = Modifier.padding(contentPadding)
        )
    }
}