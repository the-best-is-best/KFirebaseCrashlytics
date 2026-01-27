package org.company.app.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.firebase_core.AndroidKFirebaseCore
import io.github.kfirebase_crashlytics.AndroidKFirebaseCrashlytics
import io.github.sharedui.App

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidKFirebaseCore.initialization(this)
        AndroidKFirebaseCrashlytics.init()
        enableEdgeToEdge()
        setContent { App() }
    }
}


