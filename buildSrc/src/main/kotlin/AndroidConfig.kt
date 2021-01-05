import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion

fun BaseExtension.androidConfig() {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets.all {
        java.srcDirs("src/android${name.capitalize()}/kotlin")
        res.srcDirs("src/android${name.capitalize()}/res")
        resources.srcDirs("src/android${name.capitalize()}/resources")
        manifest.srcFile("src/android${name.capitalize()}/AndroidManifest.xml")
    }
}
