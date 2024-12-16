plugins {
    kotlin("jvm") version "2.0.10"
    id("com.gradle.plugin-publish") version "1.3.0"
    `java-gradle-plugin`
    `maven-publish`
}

group = "me.lucyydotp"
version = "0.1.3"

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
    api("io.papermc.paperweight:paperweight-userdev:1.7.7")
    api("xyz.jpenilla:run-task:2.3.1")
    api("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.0.0-beta4")

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

