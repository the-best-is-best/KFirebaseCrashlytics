package org.company.app.androidApp

import androidx.compose.ui.window.ComposeUIViewController
import io.github.kfirebase_crashlytics.KFirebaseCrashlyticsInit
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    KFirebaseCrashlyticsInit.initCrashlytics()
    return ComposeUIViewController { App() }
}