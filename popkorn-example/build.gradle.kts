plugins {
    kotlin("jvm")
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
