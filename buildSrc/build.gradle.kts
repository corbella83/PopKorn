plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.dokka:dokka-core:1.4.20")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.4.20")
    implementation("io.github.gradle-nexus:publish-plugin:1.0.0")
}
