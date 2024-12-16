import me.lucyydotp.mcgradle.paper.LoadOrder
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.loadOrder
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
    pluginRuntime("net.essentialsx:EssentialsX:2.20.1") {
        optional()
        loadOrder = LoadOrder.BEFORE
    }
    implementation("com.google.code.gson:gson:2.10.1")
}


repositories {
    maven("https://repo.essentialsx.net/releases/")
}

