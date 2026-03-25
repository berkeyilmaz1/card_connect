package com.berkeyilmaz.cardapp.core.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.berkeyilmaz.cardapp.R
import com.berkeyilmaz.cardapp.core.analytics.AnalyticsManager
import com.berkeyilmaz.cardapp.core.utility.safeNavigate
import com.berkeyilmaz.cardapp.core.utility.safePopBack
import com.berkeyilmaz.cardapp.domain.scan_result.model.ScanResponse
import com.berkeyilmaz.cardapp.presentation.main.contact.ContactView
import com.berkeyilmaz.cardapp.presentation.main.groups.GroupsView
import com.berkeyilmaz.cardapp.presentation.main.home.HomeView
import com.berkeyilmaz.cardapp.presentation.main.home.viewmodel.HomeViewModel
import com.berkeyilmaz.cardapp.presentation.main.more.MoreView
import com.berkeyilmaz.cardapp.presentation.main.scan.ScanView
import com.berkeyilmaz.cardapp.presentation.scan_result.ScanResultView
import com.berkeyilmaz.cardapp.presentation.profile.ProfileView
import com.berkeyilmaz.cardapp.presentation.settings.SettingsView
import com.google.gson.Gson

@Composable
fun MainNavHost(
    navController: NavHostController,
    context: android.content.Context,
    rootNavController: NavHostController,
    analyticsManager: AnalyticsManager,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.Home.route,
    ) {
        composable(Screen.Main.Home.route) {
            val viewModel = hiltViewModel<HomeViewModel>()
            val uiState by viewModel.uiState.collectAsState()
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Home") }

            val savedStateHandle = it.savedStateHandle
            LaunchedEffect(Unit) {
                savedStateHandle.get<Boolean>("contactSaved")?.let { saved ->
                    if (saved) {
                        viewModel.showSnackbar(context.getString(R.string.contact_saved_successfully))
                        savedStateHandle.remove<Boolean>("contactSaved")
                    }
                }
            }
            HomeView(
                uiState = uiState,
                onNotificationAction = { viewModel.removeNotification(it) },
                onQuickOptionClick = { route ->
                    if (route.isNotEmpty()) navController.safeNavigate(route)
                })
        }

        composable(Screen.Main.Contact.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Contacts") }
            ContactView(onContactClick = { contactId ->
                // TODO: navigate to detail
            })
        }

        composable(Screen.Main.Groups.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Groups") }
            GroupsView()
        }

        composable(Screen.Main.More.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("More") }
            MoreView(
                onNavigateProfile = { navController.safeNavigate(Screen.Main.Profile.route) },
                onNavigateSettings = { navController.safeNavigate(Screen.Main.Settings.route) },
                onSignOut = {
                    rootNavController.navigate(Screen.Auth.Graph.route) {
                        popUpTo(Screen.Main.Graph.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                })
        }

        // Full-screen
        composable(Screen.Main.Scan.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Scan") }
            ScanView(
                onScanCompleted = { scanResponse ->
                    navController.safePopBack()
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "scanResponse", scanResponse
                    )
                    Log.d("BerkeTag", "Navigating to ScanResult with response: $scanResponse")
                    navController.safeNavigate(Screen.Main.ScanResult.route)
                }, onBackPressed = { navController.safePopBack() })
        }

        composable(Screen.Main.ScanResult.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("ScanResult") }
            val scanResponseJson =
                navController.previousBackStackEntry?.savedStateHandle?.get<String>("scanResponse")
            val scanResponse = scanResponseJson?.let {
                try {
                    Gson().fromJson(Uri.decode(it), ScanResponse::class.java)
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
                    navController.safeNavigate(
                        route = Screen.Main.Home.route,
                        popUpToRoute = Screen.Main.Home.route,
                        inclusive = false
                    )
                }, onClose = { navController.safePopBack() }
            )
        }

        composable(Screen.Main.Settings.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Settings") }
            SettingsView(onNavigateBack = { navController.safePopBack() })
        }

        composable(Screen.Main.Profile.route) {
            LaunchedEffect(Unit) { analyticsManager.logScreenView("Profile") }
            ProfileView(
                onNavigateBack = { navController.safePopBack() },
                onNavigateToAuth = {
                    rootNavController.navigate(Screen.Auth.Graph.route) {
                        popUpTo(Screen.Main.Graph.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}