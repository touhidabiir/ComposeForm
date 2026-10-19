import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency

/**
 * `composeform.dependency.rules`: fails the build as soon as a module declares a project
 * dependency the architecture forbids (see CLAUDE.md, "Dependency rules"). Applied by the
 * application, Android library and JVM library convention plugins, so every module gets it.
 */
class DependencyRulesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.configurations.configureEach {
            dependencies.withType(ProjectDependency::class.java).configureEach {
                val reason = dependencyRuleViolation(from = target.path, to = path)
                if (reason != null) {
                    throw GradleException(
                        "Forbidden dependency ${target.path} -> $path: $reason " +
                            "See CLAUDE.md, \"Dependency rules\".",
                    )
                }
            }
        }
    }
}

/** A module path split into the parts the rules care about. */
private class Module(val path: String) {
    private val parts = path.removePrefix(":").split(":")

    // :feature:<name>:<layer>
    val feature: String? = if (parts.size == 3 && parts[0] == "feature") parts[1] else null

    // :feature:<name>:<layer> or :core:<slice>:<layer>; null for everything else.
    val layer: String? = if (parts.size == 3 && (parts[0] == "feature" || parts[0] == "core")) parts[2] else null

    val isCoreSliceDomain = parts.size == 3 && parts[0] == "core" && parts[2] == "domain"
}

/** Why [from] may not depend on [to], or null when the dependency is allowed. */
internal fun dependencyRuleViolation(from: String, to: String): String? {
    // Plugins add a module to some of its own internal configurations (e.g. AGP/Hilt in :app's
    // test setup). That's not an edge between two modules, so no rule applies.
    if (from == to) return null

    val source = Module(from)
    val target = Module(to)

    if (to == ":app") return "nothing may depend on :app; it assembles the other modules."
    if (from == ":app") return null

    if (target.feature != null) {
        if (source.feature == null) {
            return "only :app and feature modules may depend on a feature module."
        }
        if (source.feature != target.feature && target.layer != "navigation") {
            return "a feature may depend on another feature only through its navigation module."
        }
    }
    if (to == ":core:network" && source.layer != "data") {
        return "only data modules (and :app, to configure it) may depend on :core:network."
    }

    return when (source.layer) {
        "domain" -> if (to == ":core:model" || target.isCoreSliceDomain) {
            null
        } else {
            "a domain module may depend only on :core:model and core-slice domain modules."
        }
        "navigation" -> if (to == ":core:navigation") {
            null
        } else {
            "a navigation module may depend only on :core:navigation."
        }
        "ui" -> if (target.layer == "data") "a ui module never depends on a data module." else null
        "data" -> if (target.layer == "ui" || target.layer == "navigation") {
            "a data module never depends on a ui or navigation module."
        } else {
            null
        }
        else -> null
    }
}
