package me.lucyydotp.mcgradle

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

public interface PaperConfig {
    public val version: Property<String>
    public val userdev: Property<Boolean>
}

public fun Project.configurePaper(paperConfig: PaperConfig) {
    plugins.apply(JavaPlugin::class.java)
    plugins.apply(RunPaperPlugin::class.java)

    // Set up the dependency.
    if (paperConfig.userdev.get()) {
        // If we're using paperweight then just apply the plugin and set the dev bundle.
        plugins.apply(PaperweightUser::class.java)

        dependencies.apply {
            extensions.getByType(PaperweightUserDependenciesExtension::class.java)
                .paperDevBundle(paperConfig.version.map { "$it-R0.1-SNAPSHOT" })
        }
    } else {
        // Configure the repository and dependency manually.
        repositories.maven {
            it.url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        dependencies.addProvider(
            "implementation",
            paperConfig.version.map { "io.papermc.paper:paper-api:$it-R0.1-SNAPSHOT" })
    }

    // Set up the plugin runtime configuration.
    val pluginRuntime = configurations.create("pluginRuntime")
    configurations.named("compileOnly").configure { it.extendsFrom(pluginRuntime) }

    afterEvaluate {
        tasks.withType(RunServer::class.java).configureEach { task ->
            task.version.set(paperConfig.version)
            task.pluginJars(pluginRuntime.resolvedConfiguration.files)
        }
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
