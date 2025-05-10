package me.lucyydotp.mcgradle.paper

import org.gradle.api.artifacts.Dependency
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.HasConfigurableAttributes

/** Utilities for paper dependencies. */
public object PaperDependencyConfiguration {
    /**
     * The name of the plugin runtime configuration.
     *
     * This configuration extends `implementation`. Dependencies will be added as dependencies
     * to `paper-plugin.yml`, with their required state determined by [OPTIONAL].
     */
    public const val PLUGIN_RUNTIME: String = "pluginRuntime"

    // The attributes here are kinda hacky, as they have no effect on dependency resolution.
    // They're intentionally not registered for this reason.

    /** Whether the dependency is optional. */
    public val OPTIONAL: Attribute<OptionalState> =
        Attribute.of("me.lucyydotp.minecraft.optional", OptionalState::class.java)

    /** The order in which the dependency will be loaded relative to this plugin. */
    public val LOAD_ORDER: Attribute<LoadOrder> =
        Attribute.of("me.lucyydotp.minecraft.loadOrder", LoadOrder::class.java)

    /** Makes the dependency optional. If true, the plugin will load without this plugin installed. */
    public fun <T> T.optional() where T : Dependency, T : HasConfigurableAttributes<*> {
        optional = OptionalState.OPTIONAL
    }

    /** Whether the module is optional for the plugin to load on a server. */
    public var <T> T.optional: OptionalState where T : Dependency, T : HasConfigurableAttributes<*>
        set(value) {
            attributes {
                it.attribute(OPTIONAL, value)
            }
        }
        get() = attributes.getAttribute(OPTIONAL) ?: OptionalState.REQUIRED

    /** Whether the module is optional for the plugin to load on a server. */
    public var <T> T.loadOrder: LoadOrder where T : Dependency, T : HasConfigurableAttributes<*>
        set(value) {
            attributes {
                it.attribute(LOAD_ORDER, value)
            }
        }
        get() = attributes.getAttribute(LOAD_ORDER) ?: LoadOrder.OMIT
}

/** Whether a dependency plugin is required. */
public enum class OptionalState {
    /** The dependency is required. */
    REQUIRED,

    /** The dependency is optional. */
    OPTIONAL
}

/** The order in which a dependency should load. */
public enum class LoadOrder {
    /** Undefined order. */
    OMIT,

    /** The dependency will load before this plugin. */
    BEFORE,

    /** The dependency will load after this plugin. */
    AFTER,
}
