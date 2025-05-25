package io.github.kfirebase_crashlytics


expect class KFirebaseCrashlytics() {
    fun log(message: String)
    fun recordException(throwable: Throwable)
    fun trackHandledException(throwable: Throwable)
    fun setUserId(userId: String)
    fun setCustomKey(key: String, value: String)
    fun setCustomKey(key: String, value: Boolean)
    fun setCustomKey(key: String, value: Double)
    fun setCustomKey(key: String, value: Float)
    fun setCustomKey(key: String, value: Int)
    fun setCustomKey(key: String, value: Long)
    fun setCustomKeys(customKeys: Map<String, Any>)
    fun setCrashlyticsCollectionEnabled(enabled: Boolean)
    fun didCrashOnPreviousExecution(): Boolean
    fun sendUnsentReports()
    fun deleteUnsentReports()

}