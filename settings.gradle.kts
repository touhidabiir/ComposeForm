pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ComposeForm"
include(":app")
include(":core:designsystem")
include(":formbuilder")
include(":core:network")
include(":data")
include(":core:common")
include(":core:model")
include(":core:lead:domain")
include(":feature:lead:domain")
include(":feature:lead:data")
include(":feature:lead:ui")
include(":feature:acquisition")
