package com.berkeyilmaz.cardapp.core.utility

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Extension function to show a long-duration toast message.
 *
 * @param message The string resource ID of the message to display.
 *
 * Usage:
 * ```
 * context.showLongToast(R.string.example_message)
 * ```
 */
fun Context.showLongToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Extension function to show a short-duration toast message.
 *
 * @param message The string resource ID of the message to display.
 *
 * Usage:
 * ```
 * context.showShortToast(R.string.example_message)
 * ```
 */
fun Context.showShortToast(@StringRes message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}