package me.lucyydotp.mcgradle.paper

import io.papermc.paperweight.userdev.PaperweightUser
import io.papermc.paperweight.userdev.PaperweightUserDependenciesExtension
import me.lucyydotp.mcgradle.applyShadow
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.attributes.Attribute
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

public interface PaperConfig {
    public val version: Property<String>
    public val mainClass: Property<String>
}

public enum class OptionalState {
    REQUIRED,
    OPTIONAL
}

/** Utilities for paper dependencies. */
public object PaperDependencyConfiguration {
    /**
     * The name of the plugin runtime configuration.
     *
     * This configuration extends `implementation`. Dependencies will be added as dependencies
     * to `paper-plugin.yml`, with their required state determined by [OPTIONAL].
     */
    public const val PLUGIN_RUNTIME: String = "pluginRuntime"

    // TODO: is using an attribute the right idea here?
    public val OPTIONAL: Attribute<OptionalState> = Attribute.of("me.lucyydotp.minecraft.optional", OptionalState::class.java)

    /** Makes the dependency optional. If true, the plugin will load without this plugin installed. */
    public fun ModuleDependency.optional() {
        optional = OptionalState.OPTIONAL
    }

    /** Whether the module is optional for the plugin to load on a server. */
    public var ModuleDependency.optional: OptionalState
        set(value) {
            attributes {
                it.attribute(OPTIONAL, value)
            }
        }
        get() = attributes.getAttribute(OPTIONAL) ?: OptionalState.REQUIRED
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

    val pluginYmlTask = tasks.register<PaperPluginYmlTask>("pluginYml") {
        mustRunAfter("classes")
    }

    tasks.withType<Jar> {
        dependsOn(pluginYmlTask)
    }

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
