val libraryGroup: String by project

plugins {
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
}

kotlin {
    jvm()

    js {
        browser {}
        nodejs {}
    }

    ios() // Creates iosX64("ios") and iosArm64("ios")

    sourceSets {
        val commonMain by getting
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

        val iosMain by getting {
            //dependsOn(cinterop)
            //For now, iosMain doesn't depend on cinterop libraries.Only iosX64Main and iosArm64Main
            // Anyway, it compiles well but the IDE doesn't recognize any c-object
        }
        val iosTest by getting

    }


}



