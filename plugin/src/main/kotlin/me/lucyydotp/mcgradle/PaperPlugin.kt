package me.lucyydotp.mcgradle

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import xyz.jpenilla.runpaper.RunPaperPlugin
import xyz.jpenilla.runpaper.task.RunServer

public interface PaperConfig {
    public val version: Property<String>
}

internal fun Project.applyPaper() {
    apply<JavaPlugin>()
    apply<RunPaperPlugin>()

    val paperConfig = extensions.create<PaperConfig>("paper")

    // Set up paperweight.
    apply<PaperweightUser>()

    dependencies.apply {
        extensions.getByType<PaperweightUserDependenciesExtension>()
            .paperDevBundle(paperConfig.version.map { "$it-R0.1-SNAPSHOT" })
    }

    // Set up the plugin runtime configuration.
    val pluginRuntime = configurations.create("pluginRuntime")
    configurations.named("compileOnly").configure { it.extendsFrom(pluginRuntime) }

    afterEvaluate {
        tasks.withType<RunServer>().configureEach { task ->
            task.version.set(paperConfig.version)
            task.pluginJars(pluginRuntime.resolvedConfiguration.files)
        }
    }
}

public class PaperPlugin : Plugin<Project> {
    public override fun apply(project: Project) {
        project.applyPaper()
        project.applyShadow()
    }
}
