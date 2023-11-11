package me.lucyydotp.mcgradle.paper

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.impldep.org.yaml.snakeyaml.Yaml
import org.gradle.kotlin.dsl.getByType
import java.util.zip.ZipFile


public abstract class PaperPluginYmlTask : DefaultTask() {

    private val outDir = project.layout.buildDirectory.dir("generated/plugin-yml")

    private companion object {
        private val yaml = Yaml()
    }

    private fun Project.dependencyPluginNames() = configurations.named("pluginRuntime").get().map { ZipFile(it) }
        .mapNotNull { zipFile ->
            val entry = listOf("plugin.yml", "paper-plugin.yml").firstNotNullOfOrNull { zipFile.getEntry(it) }
                ?: return@mapNotNull null
            val parsedYaml: Map<String, Any> = yaml.load(zipFile.getInputStream(entry))
            parsedYaml["name"].toString()
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
                    "dependencies" to buildMap {
                        project.dependencyPluginNames().forEach {
                            put(
                                it, mapOf(
                                    "required" to true
                                )
                            )
                        }
                    }
                )
            )
        )
    }
}
