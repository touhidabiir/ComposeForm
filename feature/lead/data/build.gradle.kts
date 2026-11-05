plugins {
    id("composeform.android.library")
    id("composeform.hilt")
    // :core:network, Retrofit and Gson, all implementation-only.
    id("composeform.android.retrofit")
}

android {
    namespace = "com.touhid.composeform.feature.lead.data"
}

dependencies {
    implementation(project(":feature:lead:domain"))
    // The LeadCloser/Reviewer/Rejection DTOs (and mappers) both features' responses embed.
    implementation(project(":core:lead:data"))
}
