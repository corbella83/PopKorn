val lifecycleVersion = "2.3.1"
val fragmentVersion = "1.3.2"

plugins {
    id("com.android.library")
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

repositories {
    mavenCentral()
    google()
}

android {
    namespace = "cc.popkorn.androidx.viewmodel"
}

android.androidConfig()

kotlin {
    jvmToolchain(8)

    android {
        publishLibraryVariants("release", "debug")
    }

    sourceSets {
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        named("androidMain") {
            dependencies {
                implementation(project(":popkorn"))
                implementation("androidx.fragment:fragment:$fragmentVersion")
                implementation("androidx.lifecycle:lifecycle-viewmodel:$lifecycleVersion")
            }
        }

        named("androidTest") { }
    }
}
