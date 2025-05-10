plugins {
    id("me.lucyydotp.minecraft.paper")
}

version = "1.0.0-SNAPSHOT"

paper {
    version = "1.20.2"
    mainClass = "me.lucyydotp.mcgradle.papertest2.PaperTest2"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/");
}

dependencies {
    pluginRuntime(project(":test-build"))
}
