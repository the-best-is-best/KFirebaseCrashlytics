@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(libs.plugins.multiplatform)
//    alias(libs.plugins.compose.compiler)
//    alias(libs.plugins.compose)
    alias(libs.plugins.android.library)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.maven.publish)
}




apply(plugin = "maven-publish")
apply(plugin = "signing")


tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}


extra["packageNameSpace"] = "io.github.kfirebase_crashlytics"
extra["groupId"] = "io.github.the-best-is-best"
extra["artifactId"] = "kfirebase-crashlytics"
extra["version"] = "1.1.1"
extra["packageName"] = "KFirebaseCrashlytics"
extra["packageUrl"] = "https://github.com/the-best-is-best/KFirebaseCrashlytics"
extra["packageDescription"] = "KFirebaseCrashlytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Crashlytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform."
extra["system"] = "GITHUB"
extra["issueUrl"] = "https://github.com/the-best-is-best/KFirebaseCrashlytics/issues"
extra["connectionGit"] = "https://github.com/the-best-is-best/KFirebaseCrashlytics.git"

extra["developerName"] = "Michelle Raouf"
extra["developerNameId"] = "MichelleRaouf"
extra["developerEmail"] = "eng.michelle.raouf@gmail.com"


mavenPublishing {
    coordinates(
        extra["groupId"].toString(),
        extra["artifactId"].toString(),
        extra["version"].toString()
    )

    publishToMavenCentral(SonatypeHost.S01 , true)
    signAllPublications()

    pom {
        name.set(extra["packageName"].toString())
        description.set(extra["packageDescription"].toString())
        url.set(extra["packageUrl"].toString())
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        issueManagement {
            system.set(extra["system"].toString())
            url.set(extra["issueUrl"].toString())
        }
        scm {
            connection.set(extra["connectionGit"].toString())
            url.set(extra["packageUrl"].toString())
        }
        developers {
            developer {
                id.set(extra["developerNameId"].toString())
                name.set(extra["developerName"].toString())
                email.set(extra["developerEmail"].toString())
            }
        }
    }

}


signing {
    useGpgCmd()
    sign(publishing.publications)
}

val packageNameSpace = extra["packageNameSpace"].toString()
val packageName = extra["packageName"].toString()
kotlin {
    jvmToolchain(17)
    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

//    jvm()
//
//    js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//         binaries.executable()
//    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        watchosX64(),
        watchosArm64(),
        watchosSimulatorArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = packageName
            isStatic = true
            export(libs.firebase.crashlytics)
        }

        // Configure cinterop for each target
        target.compilations.getByName("main") {
            val defFileName = when (target.name) {
                "iosX64" -> "iosX64.def"
                "iosArm64" -> "iosArm64.def"
                "iosSimulatorArm64" -> "iosSimulatorArm64.def"
                "tvosX64" -> "tvosX64.def"
                "tvosArm64" -> "tvosArm64.def"
                "tvosSimulatorArm64" -> "tvosSimulatorArm64.def"
                "watchosX64" -> "watchosX64.def"
                "watchosArm64" -> "watchosArm64.def"
                "watchosSimulatorArm64" -> "watchosSimulatorArm64.def"
                else -> throw IllegalStateException("Unsupported target: ${target.name}")
            }

            val defFile = project.file("src/interop/$defFileName")
            if (defFile.exists()) {
                cinterops.create("FirebaseCrashlytics") {
                    defFile(defFile)
                    packageName = "io.github.native.kfirebase_crashlytics"
                }
            } else {
                logger.warn("Def file not found for target ${target.name}: ${defFile.absolutePath}")
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
//            implementation(compose.runtime)
//            implementation(compose.foundation)
//            implementation(compose.material3)
//            implementation(compose.components.resources)
//            implementation(compose.components.uiToolingPreview)
              api(libs.kfirebase.core)
           // implementation(libs.crashlytics)
            api(libs.touchlab.crashlytics)

        }

        commonTest.dependencies {
            implementation(kotlin("test"))
//            @OptIn(ExperimentalComposeLibrary::class)
//            implementation(compose.uiTest)
        }

        androidMain.dependencies {
//            implementation(compose.uiTooling)
//            implementation(libs.androidx.activityCompose)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.crashlytics)
            implementation(libs.firebase.common.ktx)


        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//
//        jsMain.dependencies {
//            implementation(compose.html.core)
//        }

        appleMain.dependencies {

        }

    }
}


android {
    namespace = extra["packageNameSpace"].toString()
    compileSdk = 35

    defaultConfig {
        minSdk = 21
        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
//        buildFeatures {
//            //enables a Compose tooling support in the AndroidStudio
//            compose = true
//        }
    }
}

//https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
    androidTestImplementation(libs.androidx.uitest.junit4)
    debugImplementation(libs.androidx.uitest.testManifest)
    //temporary fix: https://youtrack.jetbrains.com/issue/CMP-5864
    androidTestImplementation("androidx.test:monitor") {
        version { strictly("1.6.1") }
    }
}
//compose.desktop {
//    application {
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = packageNameSpace
//            packageVersion = "1.0.0"
//
//            linux {
//                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
//            }
//            windows {
//                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
//            }
//            macOS {
//                iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
//                bundleID = "org.company.app.desktopApp"
//            }
//        }
//    }
//}

abstract class GenerateDefFilesTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val interopDir: DirectoryProperty

    @TaskAction
    fun generate() {
        // Ensure the directory exists
        interopDir.get().asFile.mkdirs()

        // Constants
        val firebaseAnalyticsHeaders = "FirebaseCrashlytics.framework/Headers/FirebaseCrashlytics.h"

        // Map targets to their respective paths
        val targetToPath = mapOf(
            "iosX64" to "ios-arm64_x86_64-simulator",
            "iosArm64" to "ios-arm64",
            "iosSimulatorArm64" to "ios-arm64_x86_64-simulator",
            "macosX64" to "macos-arm64_x86_64",
            "macosArm64" to "macos-arm64_x86_64",
            "tvosArm64" to "tvos-arm64",
            "tvosX64" to "tvos-arm64_x86_64-simulator",
            "tvosSimulatorArm64" to "tvos-arm64_x86_64-simulator",
            "watchosArm64" to "watchos-arm64",
            "watchosX64" to "watchos-arm64_x86_64-simulator",
            "watchosSimulatorArm64" to "watchos-arm64_x86_64-simulator"
        )

        // Helper function to generate header paths
        fun headerPath(target: String): String {
            return interopDir.dir("libs/${targetToPath[target]}/$firebaseAnalyticsHeaders").get().asFile.absolutePath
        }

        // Generate headerPaths dynamically
        val headerPaths = targetToPath.mapValues { (target, _) ->
            headerPath(target)
        }

        // List of targets derived from targetToPath keys
        val iosTargets = targetToPath.keys.toList()

        // Loop through the targets and create the .def files
        iosTargets.forEach { target ->
            val headerPath = headerPaths[target] ?: return@forEach
            val defFile = File(interopDir.get().asFile, "$target.def")

            // Generate the content for the .def file
            val content = """
                language = Objective-C
                package = ${packageName.get()}
                headers = $headerPath
            """.trimIndent()

            // Write content to the .def file
            defFile.writeText(content)
            println("Generated: ${defFile.absolutePath} with headers = $headerPath")
        }
    }
}
// Register the task within the Gradle build
tasks.register<GenerateDefFilesTask>("generateDefFiles") {
    packageName.set("io.github.native.kfirebase_crashlytics")
    interopDir.set(project.layout.projectDirectory.dir("src/interop"))
}

tasks.named("build") {
    dependsOn(tasks.named("generateDefFiles"))
}