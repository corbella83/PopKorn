val lifecycleVersion = "2.2.0"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

repositories {
    mavenCentral()
    jcenter()
    google()
}

kotlin {
    jvm()
    android()

    sourceSets {
        named("androidMain") {
            dependencies {
                implementation(project(":popkorn"))
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            }
        }
    }
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets.all {
        java.srcDirs("src/android${name.capitalize()}/kotlin")
        res.srcDirs("src/android${name.capitalize()}/res")
        resources.srcDirs("src/android${name.capitalize()}/resources")
        manifest.srcFile("src/android${name.capitalize()}/AndroidManifest.xml")
    }
}
