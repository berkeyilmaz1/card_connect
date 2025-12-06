package com.berkeyilmaz.cardapp.core.navigation

import androidx.annotation.StringRes
import com.berkeyilmaz.cardapp.R

sealed class Screen(
    open val route: String, @field:StringRes open val titleRes: Int? = null
) {

    // Auth Navigation Graph
    sealed class Auth(
        route: String, @StringRes titleRes: Int? = null
    ) : Screen(route, titleRes) {

        data object Graph : Auth(
            route = "auth_graph", titleRes = null
        )

        data object SignIn : Auth(
            route = "sign_in", titleRes = R.string.sign_in
        )

        data object ForgotPassword : Auth(
            route = "forgot_password", titleRes = R.string.forgot_password
        )
    }

    // Main Navigation Graph
    sealed class Main(
        route: String, @StringRes titleRes: Int? = null
    ) : Screen(route, titleRes) {

        data object Graph : Main(
            route = "main_graph", titleRes = null
        )

        // Bottom Navigation Screens
        data object Home : Main(
            route = "home", titleRes = R.string.home
        )

        data object Contact : Main(
            route = "contacts", titleRes = R.string.contacts
        )

        data object Groups : Main(
            route = "groups", titleRes = R.string.groups
        )

        data object More : Main(
            route = "more", titleRes = R.string.more
        )

        // Full Screen Screens
        data object Settings : Main(
            route = "settings", titleRes = R.string.settings
        )

        data object Profile : Main(
            route = "profile", titleRes = R.string.profile
        )

        data object Scan : Main(
            route = "scan", titleRes = R.string.scan
        )

        data object ScanResult : Main(
            route = "scan_result", titleRes = R.string.scan_result
        )

        // Parameterized Routes
//        data class ContactDetail(val id: String) : Main(
//            route = "contact_detail/$id", titleRes = R.string.contact_detail
//        ) {
//            companion object {
//                const val ROUTE_PATTERN = "contact_detail/{id}"
//                const val ARG_ID = "id"
//
//                fun createRoute(id: String) = "contact_detail/$id"
//            }
//        }
    }
}
