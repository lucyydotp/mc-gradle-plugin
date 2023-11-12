plugins {
    id("me.lucyydotp.minecraft.paper")
}

version = "1.0.0-SNAPSHOT"

paper {
    version = "1.20.2"
    mainClass = "me.lucyydotp.mcgradle.papertest.PaperTestPlugin"
}

dependencies {
    // just imagine the repo is configured
    pluginRuntime(project(":test-build"))
}
