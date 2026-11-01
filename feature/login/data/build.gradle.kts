plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.feature.login.data"
}

dependencies {
    implementation(project(":feature:login:domain"))
    // safeApiCall, the shared Retrofit instance and the TokenProvider contract.
    implementation(project(":core:network"))
    implementation(libs.squareup.retrofit)
}
