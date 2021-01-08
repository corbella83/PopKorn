buildscript {
    val kotlinVersion by System.getProperties()
    val dokkaVersion = "0.10.1"
    val ktlintVersion = "9.4.1"

    repositories {
        mavenCentral()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:$ktlintVersion")
    }

}

allprojects {
    val libraryGroup: String by project
    val libraryVersion: String by project

    group = libraryGroup
    version = libraryVersion
}
