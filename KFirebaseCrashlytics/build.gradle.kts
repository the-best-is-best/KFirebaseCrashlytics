import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    id("co.touchlab.crashkios.crashlyticslink") version "0.9.0"
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
extra["version"] = "2.1.0"
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

    publishToMavenCentral(true)
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


    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "io.github.kfirebase_crashlytics"
        compileSdk = 36
        minSdk = 21
    }

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
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
            baseName = packageName + "Core"
        }

        // Configure cinterop for each target
//
        target.compilations.getByName("main") {
            val firCrashlytics by cinterops.creating {
                defFile("/Users/michelleraouf/Desktop/kmm/KFirebaseCrashlytics/KFirebaseCrashlytics/src/interop/firehase_crashlytics.def")
                packageName = "io.github.native.kfirebase_crashlytics"
            }

        }
    }


    // Source set declarations.
    // Declaring a target automatically creates a source set with the same name. By default, the
    // Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
    // common to share sources between related targets.
    // See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                // Add KMP dependencies here
                api(libs.kfirebase.core)
                api(libs.touchlab.crashlytics)

            }
        }

        commonTest {
            dependencies {
                //   implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.common.ktx)
            }
        }

        appleMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }

}


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
        val firebaseCrashlyticsHeaders = "FirebaseCrashlytics.h"

        //
        // Helper function to generate header paths
        fun headerPath(): String {
            return interopDir.dir("libs/$firebaseCrashlyticsHeaders").get().asFile.absolutePath
        }

        val content = """
                language = Objective-C
                package = "io.github.native.kfirebase_crashlytics"
                headers = ${headerPath()}
            """.trimIndent()
        val defFile = File(interopDir.get().asFile, "firehase_crashlytics.def")

        // Write content to the .def file
        defFile.writeText(content)
    }
}
// Register the task within the Gradle build
tasks.register<GenerateDefFilesTask>("generateDefFiles") {
    packageName.set("io.github.native.kfirebase_crashlytics")
    interopDir.set(project.layout.projectDirectory.dir("src/interop"))
}