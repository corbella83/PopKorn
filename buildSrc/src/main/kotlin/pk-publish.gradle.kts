plugins {
    `maven-publish`
    signing
    id("docs")
}

val dokkaJar by tasks.creating(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn(tasks.dokkaHtml)
}

publishing {
    publications.withType<MavenPublication> {
        val publicName = "${rootProject.name} ${name.capitalize()}"
        pom {
            name.set(publicName)
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

        artifact(dokkaJar)
    }
}

signing {
    if (!project.version.toString().endsWith("-SNAPSHOT")) {
        useGpgCmd()
        sign(publishing.publications)
    }
}
