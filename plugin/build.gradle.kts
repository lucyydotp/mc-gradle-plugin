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
//    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    api("io.papermc.paperweight:paperweight-userdev:1.5.9")
    api("xyz.jpenilla:run-task:2.2.0")
    api("com.github.johnrengelman:shadow:8.1.1")

    implementation("org.yaml:snakeyaml:2.2")
    implementation(gradleKotlinDsl())
}

gradlePlugin {
    plugins {
        create("paper") {
            id = "me.lucyydotp.minecraft.paper"
            implementationClass = "me.lucyydotp.mcgradle.paper.PaperPlugin"
        }
    }
}

publishing.repositories.maven {
    url = uri("https://maven.lucypoulton.net/releases")
    val LUCY_MAVEN_USER: String by project
    val LUCY_MAVEN_TOKEN: String by project
    credentials {
        username = LUCY_MAVEN_USER
        password = LUCY_MAVEN_TOKEN
    }
}

