plugins {
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint")
    kotlin("kapt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":popkorn"))
    kapt(project(":popkorn-compiler"))

    implementation(kotlin("stdlib"))
}
