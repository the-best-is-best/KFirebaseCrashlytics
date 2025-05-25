<h1 align="center">KFirebaseCrashlytics</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

<br>

### KFirebaseCrashlytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Crashlytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kfirebase-crashlytics)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kfirebase-crashlytics)

KFirebaseCrashlytics is available on `mavenCentral()`.
<br>

- Note add permissions notification needed android and ios

<br>

## Installation

```kotlin
commonMain.dependencies {
            implementation(projects.kFirebaseCrashlytics)
    ...
}
appleMain.dependencies{
                api(projects.kFirebaseCrashlytics)

}
 listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            export(projects.kFirebaseCrashlytics) //  need this
        }
    }
```

### iOS (Using Swift Package Manager - SPM)

Make sure to add Firebase as a dependency using Swift Package Manager (SPM).

1. Open your Xcode project.
2. Navigate to `File > Add Packages...`.
3. Enter Firebase repository URL: `https://github.com/firebase/firebase-ios-sdk`
4. Choose `FirebaseCrashlytics` and add it to your project.

### First in ios

```swift
 FirebaseApp.configure()
        KFirebaseCrashlyticsInit.shared.doInitCrashlytics()
    ...
```

### Example code

#### Can use `https://crashkios.touchlab.co/docs/crashlytics` <a href = "https://crashkios.touchlab.co/docs/crashlytics"> CrashKiOS </a>

```kotlin
 ElevatedButton(
                onClick = {
                    CrashlyticsKotlin.sendHandledException(Exception("kotlin exception"))
                }
            )
            {
                Text("Record Exception with crashlytics iso")
            }

            ...
```

### API

``` kotlin
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
```
