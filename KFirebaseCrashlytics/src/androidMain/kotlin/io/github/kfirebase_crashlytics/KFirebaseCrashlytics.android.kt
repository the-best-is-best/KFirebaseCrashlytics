package io.github.kfirebase_crashlytics

import com.google.firebase.crashlytics.FirebaseCrashlytics

actual class KFirebaseCrashlytics {
    private val fireCrashlytics = FirebaseCrashlytics.getInstance()
    actual fun log(message: String) {
        fireCrashlytics.log(message)
    }

    actual fun recordException(throwable: Throwable) {
        fireCrashlytics.recordException(throwable)
    }


    actual fun trackHandledException(throwable: Throwable) {

    }

    actual fun setUserId(userId: String) {
        fireCrashlytics.setUserId(userId)
    }

    actual fun setCustomKey(key: String, value: String) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Boolean) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Double) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Float) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Int) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKey(key: String, value: Long) {
        fireCrashlytics.setCustomKey(key, value)
    }

    actual fun setCustomKeys(customKeys: Map<String, Any>) {
        customKeys.forEach { (key, value) ->
            when (value) {
                is String -> fireCrashlytics.setCustomKey(key, value)
                is Boolean -> fireCrashlytics.setCustomKey(key, value)
                is Double -> fireCrashlytics.setCustomKey(key, value)
                is Float -> fireCrashlytics.setCustomKey(key, value)
                is Int -> fireCrashlytics.setCustomKey(key, value)
                is Long -> fireCrashlytics.setCustomKey(key, value)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    actual fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        fireCrashlytics.isCrashlyticsCollectionEnabled = enabled
    }

    actual fun didCrashOnPreviousExecution(): Boolean {
        return fireCrashlytics.didCrashOnPreviousExecution()
    }

    actual fun sendUnsentReports() {
        fireCrashlytics.sendUnsentReports()
    }

    actual fun deleteUnsentReports() {
        fireCrashlytics.deleteUnsentReports()
    }

}