package me.lucyydotp.mcgradle.paper

import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.attributes.Attribute

/** Utilities for paper dependencies. */
public object PaperDependencyConfiguration {
    /**
     * The name of the plugin runtime configuration.
     *
     * This configuration extends `implementation`. Dependencies will be added as dependencies
     * to `paper-plugin.yml`, with their required state determined by [OPTIONAL].
     */
    public const val PLUGIN_RUNTIME: String = "pluginRuntime"

    /** Whether the dependency is optional. */
    public val OPTIONAL: Attribute<OptionalState> =
        Attribute.of("me.lucyydotp.minecraft.optional", OptionalState::class.java)

    /** The order in which the dependency will be loaded relative to this plugin. */
    public val LOAD_ORDER: Attribute<LoadOrder> =
        Attribute.of("me.lucyydotp.minecraft.loadOrder", LoadOrder::class.java)

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

    /** Whether the module is optional for the plugin to load on a server. */
    public var ModuleDependency.loadOrder: LoadOrder
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
