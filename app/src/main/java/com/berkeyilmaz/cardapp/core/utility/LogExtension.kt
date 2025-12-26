package com.berkeyilmaz.cardapp.core.utility

import android.util.Log

/**
 * Extension function for logging debug messages with a tag based on the class name.
 * @param message The debug message to log.
 *
 * Usage:
 * ```
 * this.logD("This is a debug message")
 * ```
 */
fun Any.logD(message: String) {
    //TODO: Add a environment check to avoid logging in production
    val tag = this::class.simpleName ?: "APP_DEBUG"
    Log.d(tag, message)
}
