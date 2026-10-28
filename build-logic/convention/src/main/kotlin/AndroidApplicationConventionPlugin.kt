import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * `composeform.android.application`: the Android application setup for `:app`, Compose included.
 * The module itself states only its namespace, application ID, versions, build types and dependencies.
 */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

            extensions.configure<ApplicationExtension> {
                compileSdk {
                    version = release(ProjectConfig.COMPILE_SDK) {
                        minorApiLevel = ProjectConfig.COMPILE_SDK_MINOR
                    }
                }
                defaultConfig {
                    minSdk = ProjectConfig.MIN_SDK
                    targetSdk = ProjectConfig.TARGET_SDK
                }
                compileOptions {
                    sourceCompatibility = ProjectConfig.JAVA_VERSION
                    targetCompatibility = ProjectConfig.JAVA_VERSION
                }
                buildFeatures {
                    compose = true
                }
            }

            addComposeDependencies()
        }
    }
}
