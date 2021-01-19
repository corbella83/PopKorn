plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(project(":popkorn"))
    kapt(project(":popkorn-compiler"))

    implementation(kotlin("stdlib"))
}
