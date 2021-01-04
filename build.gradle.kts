buildscript {
    val kotlinVersion:String by System.getProperties()
    val dokkaVersion = "1.4.20"

    repositories {
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
    }
}

allprojects {
    val libraryGroup: String by project
    val libraryVersion: String by project

    group = libraryGroup
    version = libraryVersion

    repositories {
        mavenCentral()
        jcenter()
    }
}
