import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.internal.os.OperatingSystem
import java.io.File

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
    val isMac = OperatingSystem.current().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                Publishing the library requires macOS to be able to generate iOS artifacts.
                Run the task on a mac or use the project GitHub workflows for publication and release.
                """ .trimIndent()
            )
        }
    }
}

extra.apply {
    set("packageNameSpace", "io.github.kfirebase_crashlytics")
    set("groupId", "io.github.the-best-is-best")
    set("artifactId", "kfirebase-crashlytics")
    set("version", "2.1.1")
    set("packageName", "KFirebaseCrashlytics")
    set("packageUrl", "https://github.com/the-best-is-best/KFirebaseCrashlytics")
    set("packageDescription", "KFirebaseCrashlytics is a Kotlin Multiplatform Mobile (KMM) package designed to provide seamless integration with Firebase Crashlytics across both Android and iOS platforms. This package allows developers to easily track user events, monitor app performance, and gain insights into user behavior through a unified API, without duplicating code for each platform.")
    set("system", "GITHUB")
    set("issueUrl", "https://github.com/the-best-is-best/KFirebaseCrashlytics/issues")
    set("connectionGit", "https://github.com/the-best-is-best/KFirebaseCrashlytics.git")
    set("developerName", "Michelle Raouf")
    set("developerNameId", "MichelleRaouf")
    set("developerEmail", "eng.michelle.raouf@gmail.com")
}

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
    androidLibrary {
        namespace = packageNameSpace
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    val firebaseXCFrameworkDir = project.layout.projectDirectory.dir("src/interop/libs/FirebaseCrashlytics.xcframework")
    val packageNameProp = packageName

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
//        watchosX64(),
        watchosArm64(),
        watchosSimulatorArm64()
    ).forEach { target ->

        val targetName = target.name

        val generateDefTask = tasks.register<GenerateDefFilesTask>("generateDefFile${targetName.replaceFirstChar { it.uppercase() }}") {
            packageName.set(packageNameProp)
            this.targetName.set(targetName)
            this.xcframeworkDir.set(firebaseXCFrameworkDir)
            this.outputFile.set(project.layout.buildDirectory.file("generated/def/firebase_crashlytics_${targetName}.def"))
        }

        target.binaries.framework {
            baseName = packageName + "Core"
        }

        target.compilations.getByName("main").apply {
            val firCrashlytics by cinterops.creating {
                definitionFile.set(generateDefTask.flatMap { it.outputFile })
                packageName = "io.github.native.kfirebase_crashlytics"
            }
            val compileTaskName = "compileKotlin${targetName.replaceFirstChar { it.uppercase() }}"
            tasks.named(compileTaskName).configure {
                dependsOn(generateDefTask)
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                api(libs.kfirebase.core)
                api(libs.touchlab.crashlytics)
            }
        }
        commonTest {
            dependencies {
                // Add your test dependencies here
            }
        }
        androidMain {
            dependencies {
                implementation(project.dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.crashlytics)
                implementation(libs.firebase.common.ktx)
            }
        }
        appleMain {
            dependencies {
                // Add iOS specific dependencies here if needed
            }
        }
    }
}

abstract class GenerateDefFilesTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:Input
    abstract val targetName: Property<String>

    @get:InputDirectory
    abstract val xcframeworkDir: DirectoryProperty

    @get:OutputFile
    abstract val outputFile: RegularFileProperty

    @TaskAction
    fun generate() {
        val target = targetName.get()
        val xcDir = xcframeworkDir.get()

        val subfolder = when (target) {
            "iosArm64" -> "ios-arm64"
            "iosX64" -> "ios-arm64_x86_64-simulator"
            "iosSimulatorArm64" -> "ios-arm64_x86_64-simulator"
            "tvosArm64" -> "tvos-arm64"
            "tvosX64" -> "tvos-arm64_x86_64-simulator"
            "tvosSimulatorArm64" -> "tvos-arm64_x86_64-simulator"
            "watchosArm64" -> "watchos-arm64_arm64_32"
//            "watchosX64" -> "watchos-arm64_x86_64-simulator"
            "watchosSimulatorArm64" -> "watchos-arm64_x86_64-simulator"
            else -> throw IllegalArgumentException("Unknown target: $target")
        }

        val frameworkFile = xcDir.asFile.resolve("$subfolder/FirebaseCrashlytics.framework")
        if (!frameworkFile.exists()) {
            throw IllegalStateException("Expected framework not found: ${frameworkFile.absolutePath}")
        }

        val frameworkPath = frameworkFile.absolutePath
        val headerPath = "$frameworkPath/Headers/FirebaseCrashlytics.h"
        val headerFile = File(headerPath)

        if (!headerFile.exists()) {
            throw IllegalStateException("Expected header not found: $headerPath")
        }

        val content = """
            language = Objective-C
            package = "${packageName.get()}"
            headers = $headerPath
            compilerOpts = -F$frameworkPath
            linkerOpts = -F$frameworkPath
        """ .trimIndent()

        val defFile = outputFile.get().asFile
        defFile.parentFile.mkdirs()
        defFile.writeText(content)

        logger.lifecycle("Generated .def file for $target at ${defFile.absolutePath}")
    }
}

// Aggregator task that depends on all other GenerateDefFilesTask tasks
tasks.register("generateDefFiles") {
    group = "interop"
    description = "Generates all .def files for all targets"
    dependsOn(tasks.withType<GenerateDefFilesTask>())
}
