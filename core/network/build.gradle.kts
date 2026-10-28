plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.network"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.okhttp.logging.interceptor)
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.retrofit.converter.scalars)
    implementation(libs.squareup.retrofit.converter.gson)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
