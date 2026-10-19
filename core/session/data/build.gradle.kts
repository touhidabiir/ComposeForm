plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.session.data"
}

dependencies {
    implementation(project(":core:session:domain"))
    // safeAppCall, the shared Retrofit instance and the TokenProvider contract.
    implementation(project(":core:network"))
    implementation(libs.squareup.retrofit)
}
