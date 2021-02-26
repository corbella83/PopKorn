buildscript {
    val kotlinVersion: String by System.getProperties()

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

plugins {
    `nexus-publish`
}

allprojects {
    val libraryGroup: String by project
    val libraryVersion: String by project

    group = libraryGroup
    version = libraryVersion

    repositories {
        mavenCentral()
        jcenter()
    }
}
