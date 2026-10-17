import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.library(alias: String): Provider<MinimalExternalModuleDependency> =
    libs.findLibrary(alias).get()

/** Shared SDK levels and Java version, so a bump is a one-line change here instead of one per module. */
internal object ProjectConfig {
    const val COMPILE_SDK = 36
    const val COMPILE_SDK_MINOR = 1
    const val MIN_SDK = 24
    const val TARGET_SDK = 36

    // Not `const`: JavaVersion is an enum, and `const val` only allows primitives and String.
    val JAVA_VERSION = JavaVersion.VERSION_11
}

/** The Compose dependencies every Compose module needs, versions from the BOM. */
internal fun Project.addComposeDependencies() {
    val bom = library("androidx-compose-bom")
    dependencies.add("implementation", dependencies.platform(bom))
    dependencies.add("implementation", library("androidx-compose-ui"))
    dependencies.add("implementation", library("androidx-compose-foundation"))
    dependencies.add("implementation", library("androidx-compose-ui-tooling-preview"))
    dependencies.add("debugImplementation", library("androidx-compose-ui-tooling"))
}
