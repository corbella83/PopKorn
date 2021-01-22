val lifecycleVersion = "2.2.0"
val fragmentVersion = "1.2.5"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    id("pk-publish")
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    classifier = "javadoc"
    from(tasks.dokka)
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
    jcenter()
    google()
}

android.androidConfig()

kotlin {
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