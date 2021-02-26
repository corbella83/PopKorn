val kotlinPoetVersion = "1.7.2"
val kotlinxMetadataVersion = "0.2.0"
val apacheVersion = "2.6"
val compileTestVersion = "0.19"

plugins {
    kotlin("jvm")
    `pk-publish`
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("compiler") {
            from(components["kotlin"])
            artifact(sourcesJar)
        }
    }
}

dependencies {
    implementation(project(":popkorn"))
    implementation(kotlin("reflect"))
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:$kotlinxMetadataVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("com.google.testing.compile:compile-testing:$compileTestVersion")
    testImplementation("commons-io:commons-io:$apacheVersion")
}
