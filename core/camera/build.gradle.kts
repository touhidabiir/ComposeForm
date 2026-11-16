plugins {
    id("composeform.android.library.compose")
}

android {
    namespace = "com.touhid.composeform.camera"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.compose.material.icons.extended) // FlashOn / FlashOff
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
}
