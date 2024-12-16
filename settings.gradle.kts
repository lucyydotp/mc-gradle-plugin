rootProject.name = "gradle-mc-plugin-thing"

includeBuild("plugin")
include("test-build", "test-build-2")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}
