plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.touhid.composeform.feature.leaddashboard"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // Not :network directly - :data re-exports it via api() (see CLAUDE.md's Data layer notes),
    // so LeadListItem/NetworkResult/etc. are already resolvable through this one dependency. A
    // feature module has no reason to configure the network client (no @BaseUrl/TokenProvider
    // binding), so it never needs :network as a direct dependency the way :app does.
    implementation(project(":data"))
    implementation(project(":designsystem"))
    implementation(project(":common"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
