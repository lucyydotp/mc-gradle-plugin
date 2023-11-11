plugins {
    kotlin("jvm") version "1.9.20"
    `java-gradle-plugin`
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
    implementation("io.papermc.paperweight:paperweight-userdev:1.5.9")
}

gradlePlugin {
    plugins {
        create("paper") {
            id = "me.lucyydotp.minecraft.paper"
            implementationClass = "me.lucyydotp.mcgradle.PaperPlugin"
        }
    }
}
