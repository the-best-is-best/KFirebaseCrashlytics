package io.github.kfirebase_crashlytics

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook

object KFirebaseCrashlyticsInit {
    fun initCrashlytics() {
        enableCrashlytics()
        setCrashlyticsUnhandledExceptionHook()
    }
}