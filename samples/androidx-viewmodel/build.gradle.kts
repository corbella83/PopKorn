val appCompatVersion = "1.2.0"
val coreVersion = "1.3.2"

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    jcenter()
    google()
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
}

dependencies {
    implementation(project(":popkorn"))
    implementation(project(":popkorn-androidx-viewmodel"))
    kapt(project(":popkorn-compiler"))

    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.core:core-ktx:$coreVersion")
}
