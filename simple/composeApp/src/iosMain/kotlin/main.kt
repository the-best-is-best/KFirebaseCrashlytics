//import io.github.kfirebase_crashlytics.KFirebaseCrashlytics
import androidx.compose.ui.window.ComposeUIViewController
import io.github.kfirebase_crashlytics.KFirebaseIosInit
import io.github.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    KFirebaseIosInit().init()
    App()

}


