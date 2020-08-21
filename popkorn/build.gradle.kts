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

    ios {

        compilations.getByName("main") {
            val myInterop by cinterops.creating {
                defFile("cinterop.def")
                packageName(libraryGroup)
            }
        }

    }


    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }


        val iosMain by getting {
            //dependsOn(cinterop)
        }

        val iosX64Main by getting
        val iosArm64Main by getting

    }


}



