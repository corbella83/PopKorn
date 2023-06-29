plugins {
    kotlin("jvm")
    kotlin("kapt")
    application
}

application {
    mainClass.set("cc.popkorn.example.Main")
}

kotlin {
    jvmToolchain(8)
}

dependencies {
    implementation(project(":popkorn"))
    kapt(project(":popkorn-compiler"))

    implementation(kotlin("stdlib"))
}
