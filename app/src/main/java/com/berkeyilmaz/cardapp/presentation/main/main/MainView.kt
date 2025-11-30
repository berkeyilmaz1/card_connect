package com.berkeyilmaz.cardapp.presentation.main.main

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.presentation.main.contact.ContactView
import com.berkeyilmaz.cardapp.presentation.main.groups.GroupsView
import com.berkeyilmaz.cardapp.presentation.main.home.HomeView
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeViewModel
import com.berkeyilmaz.cardapp.presentation.main.main.scan.ScanView
import com.berkeyilmaz.cardapp.presentation.main.more.MoreView
import com.berkeyilmaz.cardapp.presentation.scan_result.ScanResultView
import com.berkeyilmaz.cardapp.presentation.settings.SettingsView

@Composable
fun MainView(onNavigateToAuth: () -> Unit = {}) {
    val navController = rememberNavController()
    val bottomTabs =
        listOf(Screen.Main.Home, Screen.Main.Contact, Screen.Main.Groups, Screen.Main.More)
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomTabs.map { it.route }) {
                BottomBar(navController, bottomTabs)
            }
        },
        floatingActionButton = {
            if (currentDestination?.route == Screen.Main.Home.route) {
                ScanFabButton(onClick = {
                    navController.navigate(Screen.Main.Scan.route)
                })
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        NavHost(
            navController = navController, startDestination = Screen.Main.Home.route
        ) {
            // ========== BOTTOM TAB SCREENS (with padding) ==========
            composable(Screen.Main.Home.route) {
                val viewModel = hiltViewModel<HomeViewModel>()
                val uiState by viewModel.uiState.collectAsState()

                // ScanResult'tan gelen başarı mesajını kontrol et
                val savedStateHandle = it.savedStateHandle
                LaunchedEffect(Unit) {
                    savedStateHandle.get<Boolean>("contactSaved")?.let { saved ->
                        if (saved) {
                            viewModel.showSnackbar(context.getString(R.string.contact_saved_successfully))
                            savedStateHandle.remove<Boolean>("contactSaved")
                        }
                    }
                }

                Box(modifier = Modifier.padding(paddingValues)) {
                    HomeView(
                        uiState = uiState,
                        onNotificationAction = { viewModel.removeNotification(it) },
                        onQuickOptionClick = { route ->
                            if (route.isNotEmpty()) {
                                navController.navigate(route)
                            }
                        })
                }
            }

            composable(Screen.Main.Contact.route) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    ContactView(
                        onContactClick = { contactId ->
                            val route = Screen.Main.ContactDetail.createRoute(contactId)
                            navController.navigate(route)
                        })
                }
            }
            composable(Screen.Main.Groups.route) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    GroupsView()
                }
            }

            composable(Screen.Main.More.route) {
                Box(modifier = Modifier.padding(paddingValues)) {
                    MoreView(onNavigateProfile = {
                        navController.navigate(Screen.Main.Profile.route)
                    }, onNavigateSettings = {
                        navController.navigate(Screen.Main.Settings.route)
                    }, onSignOut = {
                        onNavigateToAuth()
                    })
                }
            }

            // ========== FULL-SCREEN PAGES (no padding) ==========
            composable(Screen.Main.Scan.route) {
                ScanView(
                    onScanCompleted = { scanResponse ->
                        navController.popBackStack()
                        // Fotoğraf tarandıktan sonra veriyi Edit sayfasına gönder
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "scanResponse", scanResponse
                        )
                        Log.d("BerkeTag", "Navigating to ScanResult with response: $scanResponse")
                        navController.navigate(Screen.Main.ScanResult.route)
                    })
            }

            composable(Screen.Main.ScanResult.route) {
                val scanResponseJson =
                    navController.previousBackStackEntry?.savedStateHandle?.get<String>("scanResponse")

                val scanResponse = scanResponseJson?.let {
                    try {
                        com.google.gson.Gson().fromJson(Uri.decode(it), ScanResponse::class.java)
                    } catch (e: Exception) {
                        Log.e("BerkeTag", "Error deserializing ScanResponse", e)
                        null
                    }
                }

                ScanResultView(
                    scanResponse = scanResponse, onNavigateAfterSave = {
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "contactSaved", true
                        )
                        navController.popBackStack(Screen.Main.Home.route, false)
                    })
            }

            composable(Screen.Main.Settings.route) {
                SettingsView(
                    onNavigateBack = { navController.navigateUp() })
            }


            composable(Screen.Main.Profile.route) {
                // ProfileView(
                //     onNavigateBack = { navController.navigateUp() }
                // )
            }

            // ========== DETAIL SCREENS ==========
            composable(Screen.Main.ContactDetail.ROUTE_PATTERN) { backStackEntry ->
                val contactId = backStackEntry.arguments?.getString("id")
                // ContactDetailView(
                //     contactId = contactId,
                //     onNavigateBack = { navController.navigateUp() }
                // )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController, tabs: List<Screen.Main>) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        tabs.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route, onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
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
                    screen.titleRes?.let {
                        Text(text = stringResource(it))
                    }
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

@Composable
fun ScanFabButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
    ) {
        Icon(
            imageVector = Icons.Rounded.CameraAlt,
            contentDescription = stringResource(R.string.scan_button),
            tint = Color.White
        )
    }
}