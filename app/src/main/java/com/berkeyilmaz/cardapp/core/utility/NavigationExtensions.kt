package com.berkeyilmaz.cardapp.core.utility

import androidx.navigation.NavController
import androidx.navigation.NavDestination

/**
 * Extension function for safe navigation operations.
 * Prevents navigation crashes.
 *
 * @param route The destination route to navigate to
 * @param popUpToRoute Optional route to pop up to in the back stack
 * @param inclusive Whether to pop the popUpToRoute itself
 * @param singleTop Whether to prevent multiple instances of the same screen
 */
fun NavController.safeNavigate(
    route: String,
    popUpToRoute: String? = null,
    inclusive: Boolean = false,
    singleTop: Boolean = true
) {
    try {
        // Prevent navigation if already on the same route
        if (this.currentDestination?.route == route) return

        this.navigate(route) {

            // Prevents multiple instances of the same screen
            if (singleTop) {
                launchSingleTop = true
            }

            // Control the back stack
            popUpToRoute?.let { target ->
                popUpTo(target) {
                    this.inclusive = inclusive
                }
            }
        }

    } catch (_: Exception) {
        // Prevent navigation crashes
    }
}

/**
 * Safely pops the back stack.
 * Prevents crashes when back stack is empty.
 */
fun NavController.safePopBack() {
    try {
        if (this.currentDestination != null) {
            this.popBackStack()
        }
    } catch (_: Exception) {
    }
}

/**
 * Navigate to a destination while clearing the back stack up to a specific route.
 *
 * @param route The destination route to navigate to
 * @param upToRoute The route to pop up to in the back stack
 * @param inclusive Whether to include the upToRoute itself in the pop operation
 */
fun NavController.navigateAndClearBackStack(
    route: String, upToRoute: String, inclusive: Boolean = true
) {
    safeNavigate(
        route = route, popUpToRoute = upToRoute, inclusive = inclusive, singleTop = true
    )
}

/**
 * Navigate to a new destination as the root, clearing the entire back stack.
 * Ideal for transitions like Login → Home where you don't want to go back.
 *
 * @param route The destination route to navigate to as the new root
 */
fun NavController.navigateAsNewRoot(
    route: String
) {
    try {
        this.navigate(route) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    } catch (_: Exception) {
    }
}

fun NavDestination?.isInRoutes(routes: List<String>): Boolean {
    return this?.route in routes
}
