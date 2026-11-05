import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.project

/**
 * `composeform.android.retrofit`: a `data` module that calls the backend. Adds `:core:network`
 * (`safeApiCall`, the shared `Retrofit` its service is created from), Retrofit (its own service
 * interface) and Gson (`@SerializedName` on its DTOs).
 *
 * All three are `implementation`, never `api`: Retrofit and Gson stay inside the `data` module, so
 * they never reach a `ui` module or `:app`, and `:core:network` keeps OkHttp/Retrofit private too
 * instead of re-exporting them for its callers. The dependency rules plugin still applies, so
 * using this outside a `data` module fails the build (only `data` modules may depend on
 * `:core:network`). The module still applies `composeform.android.library` + `composeform.hilt`
 * itself.
 */
class RetrofitConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            dependencies.add("implementation", dependencies.project(":core:network"))
            dependencies.add("implementation", library("squareup-retrofit"))
            dependencies.add("implementation", library("google-gson"))
        }
    }
}
