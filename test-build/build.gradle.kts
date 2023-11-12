import me.lucyydotp.mcgradle.paper.LoadOrder
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.loadOrder
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.optional

plugins {
    id("me.lucyydotp.minecraft.paper")
}

version = "1.0.0-SNAPSHOT"

paper {
    version = "1.20.2"
    mainClass = "me.lucyydotp.mcgradle.papertest.PaperTestPlugin"
}

relocate {
    targetPackage = "me.lucyydotp.mcgradle.papertest.shadow"
    "com.google.gson" to "gson"
}

dependencies {
    pluginRuntime("maven.modrinth:essentialsx:2.20.1") {
        optional()
        loadOrder = LoadOrder.BEFORE
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

