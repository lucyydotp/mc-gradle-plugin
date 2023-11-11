plugins {
    kotlin("jvm") version "1.9.20"
    `java-gradle-plugin`
    `maven-publish`
}

group = "me.lucyydotp"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(8)
    explicitApi()
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())

    implementation("io.papermc.paperweight:paperweight-userdev:1.5.9")
    api("xyz.jpenilla:run-task:2.2.0")
    api("com.github.johnrengelman:shadow:8.1.1")
}

gradlePlugin {
    plugins {
        create("paper") {
            id = "me.lucyydotp.minecraft.paper"
            implementationClass = "me.lucyydotp.mcgradle.PaperPlugin"
        }
    }
}

publishing {
    repositories.maven("https://maven.lucyydotp.me/releases")
}
