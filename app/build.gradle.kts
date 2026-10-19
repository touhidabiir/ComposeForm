plugins {
    id("composeform.android.application")
    id("composeform.hilt")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.touhid.composeform"

    defaultConfig {
        applicationId = "com.touhid.composeform"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        // For BuildConfig.VERSION_CODE, sent as the X-Version header (di/AppNetworkModule.kt).
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":formbuilder"))
    implementation(project(":core:network"))
    implementation(project(":core:common"))
    implementation(project(":core:navigation"))
    // Destinations the home screen opens.
    implementation(project(":feature:lead:navigation"))
    implementation(project(":feature:acquisition:navigation"))
    // The feature data and ui modules are needed only so Hilt sees their @Modules (repository
    // bindings, DestinationHandlers) and the APK merges their manifests; :app code never imports
    // from them.
    implementation(project(":feature:lead:data"))
    implementation(project(":feature:lead:ui"))
    implementation(project(":feature:acquisition:data"))
    implementation(project(":feature:acquisition:ui"))
    implementation(project(":feature:specificform:data"))
    implementation(project(":feature:specificform:ui"))
    // Same reason as the feature data modules: only the SessionRepository binding, for Hilt.
    implementation(project(":core:session:data"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
