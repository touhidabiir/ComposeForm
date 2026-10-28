plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.feature.lead.data"
}

dependencies {
    implementation(project(":feature:lead:domain"))
    // safeAppCall, the shared Retrofit instance, and (until the acquisition slice moves) the
    // LeadCloser/Reviewer/Rejection wire models both features' responses embed.
    implementation(project(":core:network"))
    // This module declares its own Retrofit service and Gson-annotated DTOs. Both stay
    // implementation-only here, so they never reach the ui module.
    implementation(libs.squareup.retrofit)
    implementation(libs.google.gson)
}
