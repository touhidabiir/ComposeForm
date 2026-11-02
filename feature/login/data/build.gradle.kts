plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.feature.login.data"
}

dependencies {
    implementation(project(":feature:login:domain"))
    // verifyOtp starts the session there.
    implementation(project(":core:session:domain"))
    // safeApiCall and the shared Retrofit instance.
    implementation(project(":core:network"))
    implementation(libs.squareup.retrofit)
}
