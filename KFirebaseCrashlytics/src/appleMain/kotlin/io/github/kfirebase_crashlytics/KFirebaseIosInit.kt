package io.github.kfirebase_crashlytics

import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook

class KFirebaseIosInit {
    fun init() {
        enableCrashlytics()
        setCrashlyticsUnhandledExceptionHook()

    }
}