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
include(":core:lead:data")
include(":core:session:domain")
include(":core:session:data")
include(":feature:lead:domain")
include(":feature:lead:data")
include(":feature:lead:ui")
include(":feature:acquisition:domain")
include(":feature:acquisition:data")
include(":feature:acquisition:ui")
include(":feature:specificform:domain")
include(":feature:specificform:data")
include(":feature:specificform:ui")
