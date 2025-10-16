package com.berkeyilmaz.cardapp.presentation.main.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.presentation.main.contact.ContactView
import com.berkeyilmaz.cardapp.presentation.main.profile.ProfileView
import com.berkeyilmaz.cardapp.presentation.main.scan.ScanView
import com.berkeyilmaz.cardapp.presentation.ui.theme.bottomNavBarIndicatorColor
import com.berkeyilmaz.cardapp.presentation.ui.theme.bottomNavBarUnSelectedColor

@Composable
fun MainView() {
    val bottomNavController = rememberNavController()
    val bottomTabs = listOf(Screen.Scan, Screen.Contact, Screen.Profile)

    Scaffold(
        bottomBar = {
            val currentDestination =
                bottomNavController.currentBackStackEntryAsState().value?.destination
            if (currentDestination?.route in bottomTabs.map { it.route }) {
                BottomBar(bottomNavController, bottomTabs)
            }
        }) { paddingValues ->

        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Scan.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Scan.route) {
                ScanView()
            }

            composable(Screen.Contact.route) {
                ContactView()
            }

            composable(Screen.Profile.route) {
                ProfileView()
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, tabs: List<Screen>) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        tonalElevation = 4.dp,
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        tabs.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route, onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }, icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Scan -> Icons.Default.CameraAlt
                            Screen.Contact -> Icons.Default.Contacts
                            Screen.Profile -> Icons.Default.Person
                            else -> Icons.AutoMirrored.Filled.Help
                        },
                        contentDescription = screen.route,
                    )
                }, label = {
                    screen.title?.let {
                        Text(
                            screen.title
                        )
                    }
                }, colors = NavigationBarItemDefaults.colors(
                    indicatorColor = bottomNavBarIndicatorColor,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedTextColor = bottomNavBarUnSelectedColor,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = bottomNavBarUnSelectedColor
                )
            )
        }
    }
}


