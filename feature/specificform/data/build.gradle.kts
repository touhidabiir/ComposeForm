plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.feature.specificform.data"
}

dependencies {
    implementation(project(":feature:specificform:domain"))
    // safeApiCall and the shared Retrofit instance.
    implementation(project(":core:network"))
    // This module declares its own Retrofit service and a Gson-typed DTO. Both stay
    // implementation-only here, so they never reach the ui module.
    implementation(libs.squareup.retrofit)
    implementation(libs.google.gson)
}
