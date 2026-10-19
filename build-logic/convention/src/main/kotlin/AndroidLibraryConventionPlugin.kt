import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/** `composeform.android.library`: SDK levels and Java version for any Android library module. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                compileSdk {
                    version = release(ProjectConfig.COMPILE_SDK) {
                        minorApiLevel = ProjectConfig.COMPILE_SDK_MINOR
                    }
                }
                defaultConfig {
                    minSdk = ProjectConfig.MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = ProjectConfig.JAVA_VERSION
                    targetCompatibility = ProjectConfig.JAVA_VERSION
                }
            }
        }
    }
}
