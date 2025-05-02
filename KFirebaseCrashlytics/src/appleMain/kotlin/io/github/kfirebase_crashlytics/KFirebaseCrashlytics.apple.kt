package io.github.kfirebase_crashlytics

import io.github.native.kfirebase_crashlytics.FIRCrashlytics
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import platform.Foundation.NSError
import platform.Foundation.NSLocalizedDescriptionKey

@OptIn(ExperimentalForeignApi::class)
actual class KFirebaseCrashlytics {
    private val firCrashlytics = FIRCrashlytics.crashlytics()



    actual fun log(message: String) {
        firCrashlytics.log(message)

    }

    @OptIn(UnsafeNumber::class)
    actual fun recordException(throwable: Throwable) {
        val nsError = NSError.errorWithDomain(
            domain = "io.github.kfirebase_crashlytics", // يمكنك تخصيص الدومين
            code = 0,
            userInfo = mapOf(
                NSLocalizedDescriptionKey to throwable.message.orEmpty()
            )
        )

        firCrashlytics.recordError(nsError)
    }

    @OptIn(UnsafeNumber::class)
    actual fun trackHandledException(throwable: Throwable) {
        val message = "🔥 Critical non-fatal exception: ${throwable.message}"
        FIRCrashlytics.crashlytics().log(message)

        FIRCrashlytics.crashlytics().setCustomValue("fatal-level", forKey = "severity")
        FIRCrashlytics.crashlytics()
            .setCustomValue(throwable::class.simpleName ?: "Unknown", forKey = "exception_type")

        // Create NSError from exception manually
        val nsError = NSError.errorWithDomain(
            domain = "io.github.kfirebase_crashlytics",  // ← غيّر الـ domain حسب تطبيقك
            code = 0,
            userInfo = mapOf("message" to (throwable.message ?: "unknown"))
        )
        FIRCrashlytics.crashlytics().recordError(nsError)
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

