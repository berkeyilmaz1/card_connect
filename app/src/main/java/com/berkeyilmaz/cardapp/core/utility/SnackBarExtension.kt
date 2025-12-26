package com.berkeyilmaz.cardapp.core.utility

import androidx.compose.material3.SnackbarHostState

/**
 * Extension function to show a snackbar with a message and optional action label.
 *
 * @param message The message to display in the snackbar. Use String resources for localization.
 * @param actionLabel An optional label for the action button in the snackbar. Use String resources for localization.
 *
 * Usage:
 * ```
 * snackbarHostState.showSnackBar("This is a message", "Retry")
 * ```
 */
suspend fun SnackbarHostState.showSnackBar(
    message: String, actionLabel: String? = null
) {
    this.showSnackbar(
        message = message, actionLabel = actionLabel
    )
}