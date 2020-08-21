val properties = java.util.Properties()
properties.load(rootProject.file("local.properties").inputStream())

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

plugins {
    `maven-publish`
    signing
}


project.extra.set("signing.keyId", properties.getProperty("signing.keyId"))
project.extra.set("signing.secretKeyRingFile", properties.getProperty("signing.secretKeyRingFile"))
project.extra.set("signing.password", properties.getProperty("signing.password"))


publishing {

    publications.configureEach {
        if (this is MavenPublication) {
            pom {
                description.set("Powerful Dependency Injector for Kotlin")
                url.set("https://github.com/corbella83/PopKorn")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("corbella83")
                        name.set("Pau Corbella")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/corbella83/PopKorn.git")
                    developerConnection.set("scm:git:ssh://git@github.com/corbella83/PopKorn.git")
                    url.set("https://github.com/corbella83/PopKorn")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri(properties.getProperty("nexus.pro_url"))
            val snapshotsRepoUrl = uri(properties.getProperty("nexus.snap_url"))
            url = if (isReleaseVersion) releasesRepoUrl else snapshotsRepoUrl
            credentials {
                username = properties.getProperty("nexus.username")
                password = properties.getProperty("nexus.password")
            }
        }
    }

}

tasks.withType<Sign>().configureEach {
    onlyIf { isReleaseVersion }
}

signing {
    sign(publishing.publications)
}

