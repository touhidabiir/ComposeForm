import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * `composeform.hilt`: Hilt runtime + its KSP compiler. Library modules only contribute `@Module`s and
 * `@Inject` classes, so they don't need the Hilt Gradle plugin; `:app` applies that one itself.
 */
class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            dependencies.add("implementation", library("google-dagger-hilt-android"))
            dependencies.add("ksp", library("google-dagger-hilt-compiler"))
        }
    }
}
