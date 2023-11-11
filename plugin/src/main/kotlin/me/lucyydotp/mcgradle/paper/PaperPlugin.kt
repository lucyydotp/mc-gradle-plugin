package me.lucyydotp.mcgradle.paper

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import me.lucyydotp.mcgradle.applyShadow
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

/** Paper configuration. */
public interface PaperConfig {
    /**
     * The Minecraft version to use.
     * Note that this is a game version, i.e. `1.20.2`, and not an API version.
     */
    public val version: Property<String>

    /** The plugin's main class. */
    public val mainClass: Property<String>

    /**
     * The plugin's API version.
     * If not specified, it's derived from [version].
     */
    public val apiVersion: Property<String>
}

internal fun Project.applyPaper() {
    apply<JavaPlugin>()
    apply<RunPaperPlugin>()

    val paperConfig = extensions.create<PaperConfig>("paper")

    paperConfig.apiVersion.convention(paperConfig.version.map {
        it.split('.').take(2).joinToString(".")
    })

    // Set up paperweight.
    apply<PaperweightUser>()

    dependencies.apply {
        extensions.getByType<PaperweightUserDependenciesExtension>()
            .paperDevBundle(paperConfig.version.map { "$it-R0.1-SNAPSHOT" })
    }

    // Set up the plugin runtime configuration.
    val pluginRuntime = configurations.create(PaperDependencyConfiguration.PLUGIN_RUNTIME)
    configurations.named("compileOnly").configure { it.extendsFrom(pluginRuntime) }

    // Create the paper-plugin.yml task.
    val pluginYmlTask = tasks.register<PaperPluginYmlTask>("pluginYml")

    tasks.withType<Jar> {
        dependsOn(pluginYmlTask)
    }

    afterEvaluate {
        tasks.withType<RunServer>().configureEach { task ->
            task.version.set(paperConfig.version)
            task.pluginJars(pluginRuntime.resolvedConfiguration.files)
        }
    }

    tasks.named("build") {
        it.dependsOn("reobfJar")
    }
}

public class PaperPlugin : Plugin<Project> {
    public override fun apply(project: Project) {
        project.applyPaper()
        project.applyShadow()
    }
}
