import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * `composeform.jvm.library`: a pure-Kotlin module with no Android dependency (`core:model`, every
 * `domain` module). Faster to compile than an Android library, and Android APIs simply aren't on its
 * classpath, so a domain module can't accidentally reach for them.
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("composeform.dependency.rules")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = ProjectConfig.JAVA_VERSION
                targetCompatibility = ProjectConfig.JAVA_VERSION
            }
            // Kotlin's JVM target must match Java's, or the build fails with
            // "Inconsistent JVM-target compatibility".
            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.fromTarget(ProjectConfig.JAVA_VERSION.toString()))
                }
            }
        }
    }
}
