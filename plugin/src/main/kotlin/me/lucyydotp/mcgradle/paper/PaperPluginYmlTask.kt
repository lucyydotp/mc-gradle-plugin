package me.lucyydotp.mcgradle.paper

import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.loadOrder
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.optional
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.PLUGIN_RUNTIME
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalDependency
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.attributes.HasConfigurableAttributes
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getByType
import org.yaml.snakeyaml.Yaml
import java.util.zip.ZipFile

private fun Dependency.matches(id: ComponentIdentifier) = when (this) {
    is ExternalDependency -> {
        id is ModuleComponentIdentifier &&
                group == id.group &&
                name == id.module
    }

    is ProjectDependency -> {
        id is ProjectComponentIdentifier &&
                this.name == id.projectName
    }

    else -> false
}

/**
 * Generates a Paper `paper-plugin.yml` file.
 */

public abstract class PaperPluginYmlTask : DefaultTask() {

    private val outDir = project.layout.buildDirectory.dir("generated/plugin-yml")

    private companion object {
        private val yaml = Yaml()
    }

    internal data class PaperDependency(
        val name: String,
        val optional: OptionalState,
        val loadOrder: LoadOrder,
    )

    private fun Project.dependencyPlugins() = buildList {
        val config = configurations.named(PLUGIN_RUNTIME).get()

        config.incoming.dependencies.forEach { dependency ->
            if (dependency !is HasConfigurableAttributes<*>) return@forEach

            // Find the first artifact with a plugin.yml or paper-plugin.yml
            val pluginYml: Map<String, Any> = config.incoming
                .artifactView { v -> v.componentFilter { id -> dependency.matches(id) } }
                .artifacts
                .firstNotNullOfOrNull { artifact ->
                    val zipFile = ZipFile(artifact.file)
                    listOf("plugin.yml", "paper-plugin.yml")
                        .firstNotNullOfOrNull { zipFile.getEntry(it) }
                        ?.let { yaml.load(zipFile.getInputStream(it)) }
                }
                ?: run {
                    logger.warn("$dependency is not a Bukkit plugin - not including in paper-plugin.yml")
                    return@forEach
                }

            val paperDependency = PaperDependency(
                pluginYml["name"]?.toString() ?: error("$dependency has an invalid plugin YML"),
                dependency.optional,
                dependency.loadOrder,
            )

            add(paperDependency)
        }
    }

    init {
        inputs.files(project.configurations.named(PLUGIN_RUNTIME).map { it.incoming.artifacts.artifactFiles })
        outputs.dir(outDir)
        project.extensions.getByType<SourceSetContainer>()
            .named(SourceSet.MAIN_SOURCE_SET_NAME)
            .configure {
                it.output.dir(outDir)
            }
    }

    @TaskAction
    public fun createPluginYml() {
        val extension = project.extensions.getByType<PaperConfig>()

        val file = mapOf(
            "name" to project.name,
            "version" to project.version,
            "main" to extension.mainClass.get(),
            "api-version" to extension.apiVersion.get(),
            "dependencies" to mapOf(
                "server" to buildMap {
                    project.dependencyPlugins().forEach { (name, optional, loadOrder) ->
                        put(
                            name, mapOf(
                                "required" to when (optional) {
                                    OptionalState.OPTIONAL -> false
                                    OptionalState.REQUIRED -> true
                                },
                                "load" to loadOrder.name
                            )
                        )
                    }
                }
            )
        )
        outDir.get().file("paper-plugin.yml").asFile.writeText(Yaml().dump(file))
    }
}
