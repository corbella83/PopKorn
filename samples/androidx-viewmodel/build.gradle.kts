val appCompatVersion = "1.2.0"
val coreVersion = "1.3.2"

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdk = 34

    defaultConfig {
        minSdk = 15
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    namespace = "cc.popkorn.samples.androidx.viewmodel"
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":popkorn"))
    implementation(project(":popkorn-androidx-viewmodel"))
    kapt(project(":popkorn-compiler"))

    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.core:core-ktx:$coreVersion")
}
