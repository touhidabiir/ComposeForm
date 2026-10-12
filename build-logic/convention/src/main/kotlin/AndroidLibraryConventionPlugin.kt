import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
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
                    version = release(SdkLevels.COMPILE_SDK) {
                        minorApiLevel = SdkLevels.COMPILE_SDK_MINOR
                    }
                }
                defaultConfig {
                    minSdk = SdkLevels.MIN_SDK
                }
                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }
        }
    }
}
