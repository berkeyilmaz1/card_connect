package com.berkeyilmaz.cardapp.core.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun FirebaseCrashlytics.recordNonFatal(e: Exception) = recordException(e)
