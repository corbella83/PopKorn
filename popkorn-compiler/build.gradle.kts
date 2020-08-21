val kotlinPoetVersion = "1.6.0"
val kotlinxMetadataVersion = "0.1.0"
val apacheVersion = "2.7"
val compileTestVersion = "0.18"

plugins {
    kotlin("jvm")
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


repositories {
    mavenCentral()
    jcenter()
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
