package com.berkeyilmaz.cardapp.core.utility

import android.util.Patterns

/**
 * Checks if the string is a valid email format.
 * @return true if the string is a valid email, false otherwise.
 *
 */
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Checks if the string is a valid password.
 * A valid password must be at least 8 characters long and contain at least one uppercase letter,
 * one lowercase letter, one digit, and one special character.
 * @return true if the string is a valid password, false otherwise.
 *
 */
fun String.isValidPassword(): Boolean {
    val passwordRegex =
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
    return Regex(passwordRegex).matches(this)
}

