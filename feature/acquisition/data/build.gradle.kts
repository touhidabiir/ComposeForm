plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.feature.acquisition.data"
}

dependencies {
    implementation(project(":feature:acquisition:domain"))
    // safeApiCall and the shared Retrofit instance.
    implementation(project(":core:network"))
    // The LeadCloser/Reviewer/Rejection DTOs (and mappers) both features' responses embed.
    implementation(project(":core:lead:data"))
    // This module declares its own Retrofit service and Gson-annotated DTOs. Both stay
    // implementation-only here, so they never reach the ui module.
    implementation(libs.squareup.retrofit)
    implementation(libs.google.gson)
}
