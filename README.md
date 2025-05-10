# mc-gradle-plugin

A small Gradle plugin to make working with multiplatform Minecraft projects easier.

- Configures paperweight-userdev and run-paper
- Generates paper-plugin.yml files
  - Allows specifying dependencies using the `pluginRuntime` configuration
- Adds a simple DSL for shading and relocating packages via shadow

## Usage
```kts
plugins {
    id("me.lucyydotp.minecraft.paper") version "0.1.3"
}

// Sets up paperweight
paper {
    version = "1.21.5"
    mainClass = "me.lucyydotp.coolplugin.MyVeryCoolPlugin"
}

// A shorthand DSL for relocating packages
relocate {
    // The base package to relocate dependencies to
    targetPackage = "me.lucyydotp.coolplugin.shadow"
    
    // Packages to relocate - the value is appended to the target package
    // i.e. here, me.lucyydotp.cool.library becomes me.lucyydotp.coolplugin.shadow.coollib
    "me.lucyydotp.cool.library" to "coollib"
}

dependencies {
    // Adds a dependency on a plugin published as a maven package
    pluginRuntime("some.other:plugin:1.0.0") {
        // These settings control the plugin's paper-plugin.yml entry
        // If not specified, defaults to LoadOrder.EMIT, and not optional
        optional()
        loadOrder = LoadOrder.OMIT
    }
}
```


