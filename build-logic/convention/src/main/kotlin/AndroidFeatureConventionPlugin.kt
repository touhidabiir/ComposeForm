import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.project

/**
 * `composeform.android.feature`: a feature screen module. Compose + Hilt, plus what every feature
 * screen uses: the design system, the shared helpers, lifecycle-aware ViewModels and icons.
 * The module itself adds only its data source and anything specific to it.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("composeform.android.library.compose")
            pluginManager.apply("composeform.hilt")

            dependencies.add("implementation", dependencies.project(":designsystem"))
            dependencies.add("implementation", dependencies.project(":common"))
            dependencies.add("implementation", library("androidx-compose-material-icons-extended"))
            dependencies.add("implementation", library("androidx-lifecycle-viewmodel-compose"))
            dependencies.add("implementation", library("androidx-lifecycle-runtime-compose"))
            dependencies.add("implementation", library("androidx-hilt-navigation-compose"))
        }
    }
}
