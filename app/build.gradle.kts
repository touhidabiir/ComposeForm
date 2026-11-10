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
        // For BuildConfig.VERSION_CODE (the X-Version header) and the flavor's BuildConfig.BASE_URL,
        // both read in di/AppNetworkModule.kt.
        buildConfig = true
    }

    // One flavor per backend environment. Only :app has flavors: library modules have none, so
    // Gradle resolves them the same way for every flavor, and :core:network never knows which
    // environment it talks to - it gets the base URL through @BaseUrl.
    // sit and uat get their own application ID and launcher name, so all three install side by side.
    flavorDimensions += "environment"
    productFlavors {
        create("sit") {
            dimension = "environment"
            applicationIdSuffix = ".sit"
            versionNameSuffix = "-sit"
            resValue("string", "app_name", "ComposeForm SIT")
            buildConfigField("String", "BASE_URL", "\"https://api-sit.composeform.dummy/\"")
        }
        create("uat") {
            dimension = "environment"
            applicationIdSuffix = ".uat"
            versionNameSuffix = "-uat"
            resValue("string", "app_name", "ComposeForm UAT")
            buildConfigField("String", "BASE_URL", "\"https://api-uat.composeform.dummy/\"")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "ComposeForm")
            buildConfigField("String", "BASE_URL", "\"https://api.composeform.dummy/\"")
        }
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
    implementation(project(":feature:specificform:navigation"))
    // The splash opens sign-in through its result contract.
    implementation(project(":feature:login:navigation"))
    // The splash and SessionGuard read and end the session.
    implementation(project(":core:session:domain"))
    // The feature data and ui modules are needed only so Hilt sees their @Modules (repository
    // bindings, DestinationHandlers) and the APK merges their manifests; :app code never imports
    // from them.
    implementation(project(":feature:lead:data"))
    implementation(project(":feature:lead:ui"))
    implementation(project(":feature:acquisition:data"))
    implementation(project(":feature:acquisition:ui"))
    implementation(project(":feature:specificform:data"))
    implementation(project(":feature:specificform:ui"))
    implementation(project(":feature:login:data"))
    implementation(project(":feature:login:ui"))
    implementation(project(":feature:webview:ui"))
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
    // ProcessLifecycleOwner: SessionGuard checks the session each time the app comes to the foreground.
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
