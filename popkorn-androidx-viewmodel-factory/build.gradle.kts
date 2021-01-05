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

android.androidConfig()

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
