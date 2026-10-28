plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.acquisition"
}

dependencies {
    // Not :network directly - :data re-exports it via api() (see CLAUDE.md's Data layer notes).
    // See :feature:leaddashboard's build.gradle.kts for the full reasoning.
    implementation(project(":data"))
    implementation(libs.coil.compose)
}
