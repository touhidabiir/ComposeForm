plugins {
    id("composeform.android.library.compose")
}

android {
    namespace = "com.touhid.composeform.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui.graphics)
}
