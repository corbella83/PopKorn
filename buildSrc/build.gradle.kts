plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

val androidGradlePluginVersion = "7.4.2"

dependencies {
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion")
}
