package me.lucyydotp.mcgradle

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property

public interface PaperConfig {
    public val version: Property<String>
    public val userdev: Property<Boolean>
}

public fun Project.configurePaper(paperConfig: PaperConfig) {
    plugins.apply(JavaPlugin::class.java)

    // Set up the dependency.
    if (paperConfig.userdev.get()) {
        // If we're using paperweight then just apply the plugin and set the dev bundle.
        plugins.apply(PaperweightUser::class.java)

        dependencies.apply {
            extensions.getByType(PaperweightUserDependenciesExtension::class.java).paperDevBundle(paperConfig.version)
        }
    } else {
        // Configure the repository and dependency manually.
        repositories.maven {
            it.url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        dependencies.addProvider("implementation", paperConfig.version.map { "io.papermc.paper:paper-api:$it" })
    }

    configurations.create("pluginRuntime") { runtime ->
        configurations.named("compileOnly").configure { it.extendsFrom(runtime) }
    }
}

public class PaperPlugin : Plugin<Project> {
    public override fun apply(project: Project) {
        project.extensions.create("paper", PaperConfig::class.java).let {
            it.userdev.convention(false)
            project.configurePaper(it)
        }
    }
}
