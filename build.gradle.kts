buildscript {
    val kotlinVersion: String by System.getProperties()
    val dokkaVersion = "1.8.10"
    val ktlintVersion = "11.4.2"

    repositories {
        gradlePluginPortal()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:$ktlintVersion")
    }
}

allprojects {
    val libraryGroup: String by project
    val libraryVersion: String by project

    group = libraryGroup
    version = libraryVersion

    repositories {
        mavenCentral()
    }
}
