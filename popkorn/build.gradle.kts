plugins {
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("pk-publish")
}

tasks.dokkaHtml {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

publishing {
    publications {
        publications.configureEach {
            if (this is MavenPublication) {
                artifact(dokkaJar)
            }
        }
    }
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
    }

    js(IR) {
        browser {}
        nodejs {}
    }

    iosX64()
    iosArm64()

    linuxX64()
    linuxArm64()
    macosX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting

        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
        }

        val linuxX64Main by getting
        val linuxArm64Main by getting
        val macosX64Main by getting
        val mingwX64Main by getting
        val nativeMain by creating {
            dependsOn(commonMain)
            linuxX64Main.dependsOn(this)
            linuxArm64Main.dependsOn(this)
            macosX64Main.dependsOn(this)
            mingwX64Main.dependsOn(this)
        }

        val linuxX64Test by getting
        val linuxArm64Test by getting
        val macosX64Test by getting
        val mingwX64Test by getting
        val nativeTest by creating {
            dependsOn(commonTest)
            linuxX64Test.dependsOn(this)
            linuxArm64Test.dependsOn(this)
            macosX64Test.dependsOn(this)
            mingwX64Test.dependsOn(this)
        }
    }

}
