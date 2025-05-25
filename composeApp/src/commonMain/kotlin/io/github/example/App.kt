package io.github.example

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import io.github.kfirebase_crashlytics.KFirebaseCrashlytics
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val firCrashlytics = KFirebaseCrashlytics()
        LaunchedEffect(Unit) {
            firCrashlytics.setCrashlyticsCollectionEnabled(true)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedButton(
                onClick = {

                    val empty: String? = null

                    println(empty!!)


                }
            ) {
                Text("Crash")
            }

            ElevatedButton(
                onClick = {
                    firCrashlytics.log("Test Log")
                }
            ) {
                Text("Log")
            }

            ElevatedButton(
                onClick = {
                    firCrashlytics.setUserId("123456")
                }
            ) {
                Text("Set User ID")
            }
            ElevatedButton(
                onClick = {
                    firCrashlytics.recordException(Exception("Test Exception"))
                }
            )
            {
                Text("Record Exception")
            }

            ElevatedButton(
                onClick = {
                    firCrashlytics.trackHandledException(Exception("Test Handled Exception"))
                }
            )
            {
                Text("Record Exception with trackHandled")
            }

            ElevatedButton(
                onClick = {
                    CrashlyticsKotlin.sendHandledException(Exception("kotlin exception"))
                }
            )
            {
                Text("Record Exception with crashlytics iso")
            }
        }
    }
}