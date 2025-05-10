plugins {
    kotlin("jvm") version "2.0.10"
    id("com.gradle.plugin-publish") version "1.3.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "me.lucyydotp"
version = "0.1.3"

kotlin {
    jvmToolchain(17)
    explicitApi()
}

repositories {
    mavenCentral()
    gradlePluginPortal()
//    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    api("io.papermc.paperweight:paperweight-userdev:2.0.0-beta.16")
    api("xyz.jpenilla:run-task:2.3.1")
    api("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.0.0-beta13")

    implementation("org.yaml:snakeyaml:2.2")
    implementation(gradleKotlinDsl())
}

gradlePlugin {
    website = "https://github.com/lucyydotp/mc-gradle-plugin"
    vcsUrl = "https://github.com/lucyydotp/mc-gradle-plugin"

    plugins {
        create("paper") {
            id = "me.lucyydotp.minecraft.paper"
            displayName = "me.lucyydotp.minecraft.paper"
            description = "Utilities for developing Paper plugins"
            tags = setOf("minecraft", "paper")
            implementationClass = "me.lucyydotp.mcgradle.paper.PaperPlugin"
        }
    }
}
