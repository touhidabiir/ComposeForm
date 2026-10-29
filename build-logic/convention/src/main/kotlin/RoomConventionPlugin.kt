import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.project

/**
 * `composeform.android.room`: a `data` module that keeps its own Room database. Adds Room + its
 * KSP compiler, exports the schema to the module's `schemas/` folder (commit it: migrations are
 * written and tested against it), and adds `:core:database` for `buildRoomDatabase`/`safeDbCall`.
 * The module still applies `composeform.android.library` + `composeform.hilt` itself.
 */
class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            extensions.configure<KspExtension> {
                arg("room.schemaLocation", "$projectDir/schemas")
            }

            dependencies.add("implementation", dependencies.project(":core:database"))
            dependencies.add("implementation", library("androidx-room-runtime"))
            dependencies.add("implementation", library("androidx-room-ktx"))
            dependencies.add("ksp", library("androidx-room-compiler"))
        }
    }
}
