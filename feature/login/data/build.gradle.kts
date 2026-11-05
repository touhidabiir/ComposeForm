plugins {
    id("composeform.android.library")
    id("composeform.hilt")
    // :core:network, Retrofit and Gson, all implementation-only.
    id("composeform.android.retrofit")
}

android {
    namespace = "com.touhid.composeform.feature.login.data"
}

dependencies {
    implementation(project(":feature:login:domain"))
    // verifyOtp starts the session there.
    implementation(project(":core:session:domain"))
}
