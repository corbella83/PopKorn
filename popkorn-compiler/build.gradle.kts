val kotlinPoetVersion = "1.14.2"
val kotlinxMetadataVersion = "0.5.0"
val apacheVersion = "2.13.0"
val compileTestVersion = "0.21.0"

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    id("pk-publish")
    id("org.jlleitschuh.gradle.ktlint")
}

tasks.dokkaHtml {
    outputDirectory.set(buildDir.resolve("javadoc"))
}

kotlin {
    jvmToolchain(8)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("compiler") {
            from(components["kotlin"])
            artifact(dokkaJar)
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(project(":popkorn"))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:$kotlinxMetadataVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.testing.compile:compile-testing:$compileTestVersion")
    testImplementation("commons-io:commons-io:$apacheVersion")
}
