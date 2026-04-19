package io.github.sharedui

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    KFirebaseCrashlyticsInit.initCrashlytics()
    return ComposeUIViewController { App() }
}