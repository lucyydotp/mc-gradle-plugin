import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.optional

plugins {
    id("me.lucyydotp.minecraft.paper")
}

paper {
    version = "1.20.2"
    mainClass = "me.lucyydotp.mcgradle.papertest.PaperTestPlugin"
}

relocate {
    targetPackage = "me.lucyydotp.mcgradle.papertest.shadow"
    "com.google.gson" to "gson"
}

dependencies {
    // just imagine the repo is configured
    pluginRuntime("maven.modrinth:essentialsx:2.20.1") {
        optional()
    }
    shadow("com.google.code.gson:gson:2.10.1")
}


repositories {
    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

