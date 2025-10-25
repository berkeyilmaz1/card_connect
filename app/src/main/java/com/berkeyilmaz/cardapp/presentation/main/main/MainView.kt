package com.berkeyilmaz.cardapp.presentation.main.main

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.manager.TextRecognizerManager
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.data.model.ApiResult
import com.berkeyilmaz.cardapp.presentation.main.contact.ContactView
import com.berkeyilmaz.cardapp.presentation.main.home.HomeView
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeViewModel
import com.berkeyilmaz.cardapp.presentation.main.main.scan.ScanView
import com.berkeyilmaz.cardapp.presentation.main.more.MoreView
import com.berkeyilmaz.cardapp.presentation.settings.SettingsView
import com.berkeyilmaz.cardapp.presentation.ui.theme.bottomNavBarIndicatorColor
import com.berkeyilmaz.cardapp.presentation.ui.theme.bottomNavBarUnSelectedColor
import kotlinx.coroutines.launch

@Composable
fun MainView() {
    val navController = rememberNavController()
    val bottomTabs = listOf(Screen.Contact, Screen.Home, Screen.More)
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomTabs.map { it.route }) {
                BottomBar(navController, bottomTabs)
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Home.route) {
                ScanFabButton(onClick = {
                    navController.navigate(Screen.Scan.route)
                })
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier  // padding'i buradan kaldır
        ) {
            composable(Screen.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                val uiState by viewModel.uiState.collectAsState()
                // Her ekrana kendi padding'ini ver
                Box(modifier = Modifier.padding(paddingValues)) {
                    HomeView(
                        uiState, onNotificationAction = { viewModel.removeNotification(it) })
                }
            }

            composable(Screen.Contact.route) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    ContactView()
                }
            }

            composable(Screen.More.route) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    MoreView(
                        onNavigateProfile = { navController.navigate(Screen.Profile.route) },
                        onNavigateSettings = { navController.navigate(Screen.Settings.route) },
                    )
                }
            }

            composable(Screen.Scan.route) {
                // Scan full-screen olabilir, padding yok
                ScanView(onPhotoCaptured = { uri ->
                    Log.d("BerkeTag", "Photo saved at: $uri")
                    coroutineScope.launch {
                        val result =
                            TextRecognizerManager.recognizeTextFromUri(context = context, uri = uri)
                        val recognizedText = (result as ApiResult.Success).data
                        Log.d("BerkeTag", "Recognized Text: ${recognizedText?.text}")
                    }
                    navController.popBackStack()
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "capturedPhotoUri", uri.toString()
                    )
                })
            }

            composable(Screen.Settings.route) {
                // Settings full-screen, padding yok
                SettingsView(
                    onNavigateBack = { navController.navigateUp() })
            }

            composable(Screen.Profile.route) {
                // Profile full-screen, padding yok
                // ProfileView()
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
                        Screen.Home -> Icons.Outlined.Home
                        Screen.Contact -> Icons.Outlined.Contacts
                        Screen.More -> Icons.Outlined.MoreHoriz
                        else -> Icons.AutoMirrored.Outlined.Help
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

@Composable
fun ScanFabButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            Icons.Rounded.CameraAlt, stringResource(R.string.scan_button), tint = Color.White
        )
    }
}


