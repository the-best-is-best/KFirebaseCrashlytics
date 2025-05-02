
package io.github.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin
import io.github.kfirebase_crashlytics.KFirebaseCrashlytics
import io.github.sample.theme.AppTheme

@Composable
internal fun App() = AppTheme {
    val firCrashlytics = KFirebaseCrashlytics()
    LaunchedEffect(Unit){
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

                val empty:String? = null

                    println(empty!!)


            }
        ){
            Text("Crash")
        }

        ElevatedButton(
            onClick = {
                firCrashlytics.log("Test Log")
            }
        ){
            Text("Log")
        }

        ElevatedButton(
            onClick = {
                firCrashlytics.setUserId("123456")
            }
        ){
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
