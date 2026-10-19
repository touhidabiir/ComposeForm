plugins {
    id("composeform.android.library.compose")
}

android {
    namespace = "com.touhid.composeform.common"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(libs.androidx.compose.material.icons.extended)
}
