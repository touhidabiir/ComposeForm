plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.touhid.composeform.network"
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.squareup.okio)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging.interceptor)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.scalars)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.squareup.retrofit.adapter.rxjava2)
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.kotlinx.coroutines.core)
    ksp(libs.google.dagger.hilt.compiler)
    testImplementation(libs.junit)
}
