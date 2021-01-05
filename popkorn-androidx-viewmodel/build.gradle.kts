val lifecycleVersion = "2.2.0"

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
    jvm()
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
                implementation(project(":popkorn-androidx-viewmodel-factory"))
                implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
            }
        }

        named("androidTest") { }
    }
}
