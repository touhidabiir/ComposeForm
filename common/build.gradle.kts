plugins {
    id("composeform.android.library.compose")
}

android {
    namespace = "com.touhid.composeform.common"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(libs.androidx.compose.material.icons.extended)
}
