package com.berkeyilmaz.cardapp.presentation.main.widgets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.core.utility.safeNavigate
import kotlin.collections.forEach

@Composable
fun BottomBar(navController: NavHostController, tabs: List<Screen.Main>) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        tabs.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route, onClick = {
                    navController.safeNavigate(
                        route = screen.route,
                        popUpToRoute = navController.graph.startDestinationRoute,
                        inclusive = false,
                        singleTop = true
                    )
                }, icon = {
                    Icon(
                        imageVector = when (screen) {
                            Screen.Main.Home -> Icons.Outlined.Home
                            Screen.Main.Contact -> Icons.Outlined.Contacts
                            Screen.Main.More -> Icons.Outlined.MoreHoriz
                            Screen.Main.Groups -> Icons.Outlined.Groups
                            else -> Icons.AutoMirrored.Outlined.Help
                        },
                        contentDescription = screen.titleRes?.let { stringResource(it) },
                    )
                }, label = {
                    screen.titleRes?.let { Text(stringResource(it)) }
                }, colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}