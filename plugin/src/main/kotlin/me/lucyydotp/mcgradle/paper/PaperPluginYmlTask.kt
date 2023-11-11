package me.lucyydotp.mcgradle.paper

import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.PLUGIN_RUNTIME
import me.lucyydotp.mcgradle.paper.PaperDependencyConfiguration.optional
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml
import org.gradle.kotlin.dsl.getByType
import java.util.zip.ZipFile

/**
 * Generates a Paper `paper-plugin.yml` file.
 */

public abstract class PaperPluginYmlTask : DefaultTask() {

    private val outDir = project.layout.buildDirectory.dir("generated/plugin-yml")

    private companion object {
        private val yaml = Yaml()
    }

    private fun Project.dependencyPlugins() = buildList {
        val config = configurations.named(PLUGIN_RUNTIME).get()
        config.dependencies.forEach {
            val optional = (it as? ModuleDependency ?: return@forEach).optional
            config.fileCollection(it).forEach files@{ file ->
                val zipFile = ZipFile(file)
                val entry = listOf("plugin.yml", "paper-plugin.yml").firstNotNullOfOrNull { zipFile.getEntry(it) }
                    ?: return@files
                val parsedYaml: Map<String, Any> = yaml.load(zipFile.getInputStream(entry))
                add(parsedYaml["name"].toString() to optional)
            }
        }
    }

    init {
        outputs.dir(outDir)
    }

    @TaskAction
    public fun createPluginYml() {
        val extension = project.extensions.getByType<PaperConfig>()

        project.extensions.getByType<SourceSetContainer>()
            .named(SourceSet.MAIN_SOURCE_SET_NAME)
            .configure {
                it.output.dir(outDir)
            }

        outDir.get().file("paper-plugin.yml").asFile.writeText(
            Yaml().dump(
                mapOf(
                    "name" to project.name,
                    "version" to project.version,
                    "main" to extension.mainClass.get(),
                    "dependencies" to mapOf(
                        "server" to buildMap {
                            project.dependencyPlugins().forEach { (name, optional) ->
                                put(
                                    name, mapOf(
                                        "required" to when (optional) {
                                            OptionalState.OPTIONAL -> false
                                            OptionalState.REQUIRED -> true
                                        }
                                    )
                                )
                            }
                        }
                    )
                )
            )
        )
    }
}
