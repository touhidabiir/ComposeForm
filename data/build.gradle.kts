plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.touhid.composeform.data"
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
}

dependencies {
    // api, not implementation: AppRepository's own public methods return NetworkResult<T> where
    // NetworkResult and T (the model classes) are :network-owned types - any consumer of :data
    // needs them resolvable on its own classpath to use those return values at all, the same
    // reason NIA exposes :core:model as api from :core:data. This must stay api even though :app
    // currently also depends on :network directly for unrelated reasons (@BaseUrl/TokenProvider
    // config) - a future module depending on :data alone (e.g. a :feature:leaddashboard with no
    // reason to configure the network client) would otherwise fail to resolve NetworkResult.
    api(project(":network"))
    implementation(libs.google.dagger.hilt.android)
    ksp(libs.google.dagger.hilt.compiler)
    testImplementation(libs.junit)
}
