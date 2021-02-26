import io.github.gradlenexus.publishplugin.NexusPublishExtension
import java.lang.System.getenv

plugins {
    id("io.github.gradle-nexus.publish-plugin")
}

val user = "${properties["oss.user"] ?: getenv("OSS_USER") ?: ""}"

val pass = "${properties["oss.token"] ?: getenv("OSS_TOKEN") ?: ""}"

val profileId = "${properties["oss.stagingProfileId"] ?: getenv("OSS_STAGING_PROFILE_ID") ?: ""}"

if (listOf(user, pass, profileId).any(String::isBlank)) {
    println("There are missing properties that are necessary to publish to Sonatype")
}

configure<NexusPublishExtension> {
    repositories {
        sonatype {
            username.set(user)
            password.set(pass)
            stagingProfileId.set(profileId)
        }
    }
}
