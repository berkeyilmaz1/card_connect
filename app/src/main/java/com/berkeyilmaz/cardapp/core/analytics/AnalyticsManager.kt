package com.berkeyilmaz.cardapp.core.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsManager @Inject constructor(
    private val analytics: FirebaseAnalytics
) {
    fun setAnalyticsEnabled(enabled: Boolean) {
        analytics.setAnalyticsCollectionEnabled(enabled)
    }

    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
        })
    }

    fun logScanCard() {
        analytics.logEvent("scan_card", null)
    }

    fun logContactAdded() {
        analytics.logEvent("contact_added", null)
    }

    fun logContactDeleted() {
        analytics.logEvent("contact_deleted", null)
    }
}
