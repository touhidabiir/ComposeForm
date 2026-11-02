plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.session.data"
}

dependencies {
    implementation(project(":core:session:domain"))
    // TokenProvider, where the tokens live.
    implementation(project(":core:network"))
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
