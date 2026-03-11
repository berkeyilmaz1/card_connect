package com.berkeyilmaz.cardapp.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.berkeyilmaz.cardapp.core.constants.BOTTOM_TABS
import com.berkeyilmaz.cardapp.core.navigation.MainNavHost
import com.berkeyilmaz.cardapp.core.navigation.Screen
import com.berkeyilmaz.cardapp.core.utility.isInRoutes
import com.berkeyilmaz.cardapp.core.utility.safeNavigate
import com.berkeyilmaz.cardapp.presentation.chat.ChatView
import com.berkeyilmaz.cardapp.presentation.chat.viewmodel.ChatViewModel
import com.berkeyilmaz.cardapp.presentation.main.widgets.AiFabButton
import com.berkeyilmaz.cardapp.presentation.main.widgets.BottomBar
import com.berkeyilmaz.cardapp.presentation.main.widgets.ScanFabButton

@SuppressLint("UnrememberedGetBackStackEntry")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    rootNavController: androidx.navigation.NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val bottomTabsRoutes = BOTTOM_TABS.map { it.route }
    val mainUiState by mainViewModel.uiState.collectAsState()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    LaunchedEffect(mainUiState.isAiSheetExpanded) {
        if (mainUiState.isAiSheetExpanded) {
            sheetState.expand()
        }
    }
    LaunchedEffect(mainUiState.isAiSheetOpen) {
        sheetState.expand()
    }

    Scaffold(bottomBar = {
        if (currentDestination.isInRoutes(bottomTabsRoutes)) {
            BottomBar(navController, BOTTOM_TABS)
        }
    }, floatingActionButton = {
        when (currentDestination?.route) {
            Screen.Main.Groups.route -> {
                AiFabButton { mainViewModel.onAiFabClicked() }
            }

            Screen.Main.Home.route -> {
                ScanFabButton {
                    navController.safeNavigate(Screen.Main.Scan.route)
                }
            }
        }
    }) { padding ->
        Box(Modifier.padding(padding)) {
            MainNavHost(navController, context, rootNavController, mainViewModel.analyticsManager)
        }

        if (mainUiState.isAiSheetOpen) {
            ModalBottomSheet(
                onDismissRequest = mainViewModel::onSheetDismiss,
                sheetState = sheetState,
                modifier = Modifier.fillMaxSize()
            ) {
                ChatView(
                    chatViewModel = chatViewModel,
                )
            }
        }
    }
}
