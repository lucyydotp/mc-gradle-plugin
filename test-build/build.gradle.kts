plugins {
    id("me.lucyydotp.minecraft.paper")
}

paper {
    version = "1.20.2-R0.1-SNAPSHOT"
//    userdev = true
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

dependencies {
    pluginRuntime("maven.modrinth:essentialsx:2.20.1")
}
