plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
    google()
}

val androidGradlePluginVersion = "4.0.2"

dependencies {
    implementation("com.android.tools.build:gradle:$androidGradlePluginVersion")
}
