package me.lucyydotp.mcgradle

import com.github.jengelman.gradle.plugins.shadow.ShadowJavaPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named

/** An alternative way of specifying relocations. */
public abstract class RelocateExtension {
    /** The root of the new package to relocate classes to. */
    public abstract val targetPackage: Property<String>

    /** A map of full package names to a short package name that will be appended to [targetPackage]. */
    public abstract val relocations: MapProperty<String, String>

    /** Relocates a package. */
    public infix fun String.to(path: String) {
        relocations.put(this, path)
    }
}

/**
 * Sets up shading for the project using shadow:
 * - Applies the [ShadowJavaPlugin].
 * - Creates the `shadow` configuration for shaded dependencies, that extends `implementation`.
 * - Creates the [`relocate` extension][RelocateExtension], and configures shadow to use it.
 * - Makes the `build` task depend on `shadowJar`.
 */
internal fun Project.applyShadow() {
    val relocateExtension = project.extensions.create<RelocateExtension>("relocate")

    val shadowConfiguration = configurations.create("shadow")
    configurations.named("implementation").configure { it.extendsFrom(shadowConfiguration) }

    apply<ShadowJavaPlugin>()

    val shadow = tasks.named<ShadowJar>("shadowJar") {
        configurations = listOf(shadowConfiguration)
    }

    afterEvaluate {
        tasks.named<ShadowJar>("shadowJar") {
            relocateExtension.relocations.get().forEach { (old, new) ->
                relocate(old, "${relocateExtension.targetPackage.get()}.$new")
            }
        }
    }

    tasks.named("build") {
        it.dependsOn(shadow)
    }
}
