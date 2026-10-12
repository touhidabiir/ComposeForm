import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
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
                    version = release(SdkLevels.COMPILE_SDK) {
                        minorApiLevel = SdkLevels.COMPILE_SDK_MINOR
                    }
                }
                defaultConfig {
                    minSdk = SdkLevels.MIN_SDK
                    targetSdk = SdkLevels.TARGET_SDK
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
                buildFeatures {
                    compose = true
                }
            }

            addComposeDependencies()
        }
    }
}
