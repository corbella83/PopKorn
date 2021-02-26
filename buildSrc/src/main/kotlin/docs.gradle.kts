import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
}

tasks {
    withType<DokkaTask>() {
        dokkaSourceSets.configureEach {
            includes.from(listOf("README.md"))
        }
    }

    withType<DokkaMultiModuleTask>().configureEach {
        val dokkaDir = buildDir.resolve("dokka")
        outputDirectory.set(dokkaDir)

        doLast {
            dokkaDir.resolve("-modules.html").renameTo(dokkaDir.resolve("index.html"))
        }
    }
}
