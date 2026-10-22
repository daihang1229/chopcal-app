package com.chopcal.app.core.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.rainbow.compose.core.log.ComposeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase Analytics 实现。
 */
@Singleton
class FirebaseAnalyticsTracker @Inject constructor(
    @ApplicationContext context: Context,
) : AnalyticsTracker {

    private val analytics = FirebaseAnalytics.getInstance(context)

    override fun setUserId(userId: String) {
        ComposeLog.d("FirebaseAnalytics: setUserId $userId")
        analytics.setUserId(userId)
    }

    override fun logEvent(name: String, params: Map<String, Any>) {
        ComposeLog.d("FirebaseAnalytics: logEvent $name, params=$params")
        val bundle = Bundle()
        params.forEach { (key, value) -> bundle.putTyped(key, value) }
        analytics.logEvent(name, bundle)
    }
}

private fun Bundle.putTyped(key: String, value: Any) {
    when (value) {
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is Long -> putLong(key, value)
        is Double -> putDouble(key, value)
        is Float -> putFloat(key, value)
        is Boolean -> putBoolean(key, value)
        else -> putString(key, value.toString())
    }
}
