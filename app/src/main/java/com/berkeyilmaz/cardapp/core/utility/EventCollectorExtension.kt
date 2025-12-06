package com.berkeyilmaz.cardapp.core.utility

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.berkeyilmaz.cardapp.core.common.UiEvent
import kotlinx.coroutines.flow.Flow

/**
 * A Composable function that collects UI events from a Flow and handles navigation,
 * snackbar display, and toast messages.
 * @param eventFlow A Flow of UiEvent to be collected.
 * @param navController An optional NavController for handling navigation events.
 * @param snackBarHostState An optional SnackbarHostState for displaying snackbars.
 *
 * Usage:
 * ```
 * CollectUiEvent(
 *    eventFlow = viewModel.uiEventFlow,
 *    navController = navController,
 *    snackBarHostState = snackbarHostState
 *    )
 *    ```
 */
@Composable
fun CollectUiEvent(
    eventFlow: Flow<UiEvent>,
    navController: NavController? = null,
    snackBarHostState: SnackbarHostState? = null
) {
    /* Get the current context */
    val context = LocalContext.current

    /* Launch a coroutine to collect events from the eventFlow */
    LaunchedEffect(eventFlow) {
        eventFlow.collect { event ->
            when (event) {
                is UiEvent.Navigate -> navController?.navigate(event.route)

                UiEvent.NavigateBack -> navController?.popBackStack()

                is UiEvent.ShowSnackBar -> snackBarHostState?.showSnackBar(
                    event.message, event.actionLabel
                )

                is UiEvent.ShowToast -> context.showShortToast(event.message)
            }
        }
    }
}