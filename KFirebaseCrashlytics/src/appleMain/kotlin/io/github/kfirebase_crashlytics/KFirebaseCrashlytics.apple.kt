package io.github.kfirebase_crashlytics

import io.github.native.kfirebase_crashlytics.FIRCrashlytics
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSError
import platform.Foundation.NSException
import platform.Foundation.NSLocalizedDescriptionKey
import com.rickclephas.kmp.nsexceptionkt.core.asNSException
@OptIn(ExperimentalForeignApi::class)
actual class KFirebaseCrashlytics {
    private val firCrashlytics = FIRCrashlytics.crashlytics()



    actual fun log(message: String) {
        firCrashlytics.log(message)

    }

    actual fun recordException(throwable: Throwable) {
     firCrashlytics.recordError(throwable)
    }

    actual fun setUserId(userId: String) {
        firCrashlytics.setUserID(userId)
    }

    actual fun setCustomKey(key: String, value: String) {
        firCrashlytics.setCustomValue(value, forKey = key)
    }

    actual fun setCustomKey(key: String, value: Boolean) {
        firCrashlytics.setCustomValue(value.toString(), forKey = key)
    }

    actual fun setCustomKey(key: String, value: Double) {
        firCrashlytics.setCustomValue(value.toString(), forKey = key)
    }

    actual fun setCustomKey(key: String, value: Float) {
        firCrashlytics.setCustomValue(value.toString(), forKey = key)
    }

    actual fun setCustomKey(key: String, value: Int) {
        firCrashlytics.setCustomValue(value.toString(), forKey = key)
    }

    actual fun setCustomKey(key: String, value: Long) {
        firCrashlytics.setCustomValue(value.toString(), forKey = key)
    }

    actual fun setCustomKeys(customKeys: Map<String, Any>) {
        customKeys.forEach { (key, value) ->
            when (value) {
                is String -> firCrashlytics.setCustomValue(value, forKey = key)
                is Boolean -> firCrashlytics.setCustomValue(value.toString(), forKey = key)
                is Double -> firCrashlytics.setCustomValue(value.toString(), forKey = key)
                is Float -> firCrashlytics.setCustomValue(value.toString(), forKey = key)
                is Int -> firCrashlytics.setCustomValue(value.toString(), forKey = key)
                is Long -> firCrashlytics.setCustomValue(value.toString(), forKey = key)
                else -> throw IllegalArgumentException("Unsupported type")
            }
        }
    }

    actual fun setCrashlyticsCollectionEnabled(enabled: Boolean) {
        firCrashlytics.setCrashlyticsCollectionEnabled(enabled)
    }

    actual fun didCrashOnPreviousExecution(): Boolean {
     return firCrashlytics.didCrashDuringPreviousExecution()
    }

    actual fun sendUnsentReports() {
        firCrashlytics.sendUnsentReports()

    }

    actual fun deleteUnsentReports() {
        firCrashlytics.deleteUnsentReports()
    }
}

