import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.touhid.composeform.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    // compileOnly: the root build.gradle.kts already puts AGP on the build classpath (apply false),
    // so the plugins below apply it by id at runtime instead of bundling a second copy.
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    // For KspExtension in RoomConventionPlugin; KSP itself is on the build classpath via the root apply false.
    compileOnly(libs.ksp.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "composeform.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "composeform.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "composeform.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "composeform.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "composeform.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("hilt") {
            id = "composeform.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("androidRoom") {
            id = "composeform.android.room"
            implementationClass = "RoomConventionPlugin"
        }
        register("dependencyRules") {
            id = "composeform.dependency.rules"
            implementationClass = "DependencyRulesPlugin"
        }
    }
}

// Android Studio's "Clean and Assemble Project with Tests" (and "Rebuild") also runs this included
// build's own clean in the same invocation, after its plugin jar was already built and loaded to
// configure the main build. Gradle then can't order that clean against the jar it destroys and stops
// with "Unable to make progress running work". Our clean therefore deletes nothing; a stale
// build-logic is rebuilt automatically whenever its sources change, and
// `rm -rf build-logic/convention/build` clears it by hand if ever needed.
tasks.named<Delete>("clean") {
    setDelete(emptyList<Any>())
}
