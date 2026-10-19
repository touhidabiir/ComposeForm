plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.acquisition"
}

dependencies {
    // Not :core:network directly - :data re-exports it via api() (see CLAUDE.md's Data layer notes).
    implementation(project(":data"))
    implementation(libs.coil.compose)
}
