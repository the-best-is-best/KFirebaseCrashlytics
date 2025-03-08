import UIKit
import ComposeApp
import FirebaseCore


@main
class AppDelegate: UIResponder, UIApplicationDelegate {
    
 

    
    var window: UIWindow?

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Configure Firebase
        FirebaseApp.configure()
        
        // Create window and root view controller
        window = UIWindow(frame: UIScreen.main.bounds)
        if let window = window {
            window.rootViewController = MainKt.MainViewController()
            window.makeKeyAndVisible()
        }

        return true
    }
}

class NSExceptionWrapper: NSObject {
    private let exception: NSException
    
    init(exception: NSException) {
        self.exception = exception
    }
    
    // Implementing KotlinThrowable properties
    var message: String? {
        return exception.reason
    }
    }
