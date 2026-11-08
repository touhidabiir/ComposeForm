plugins {
    id("composeform.android.library")
}

android {
    namespace = "com.touhid.composeform.feature.login.navigation"
}

dependencies {
    // api: OpenLogin extends ActivityResultContract, so callers need it on their classpath too.
    api(libs.androidx.activity.compose)
}
