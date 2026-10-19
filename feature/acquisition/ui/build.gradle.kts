plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.acquisition.ui"
}

dependencies {
    // The ui layer sees only the acquisition domain (models + AcquisitionRepository interface).
    // The implementation lives in :feature:acquisition:data, which :app depends on so Hilt can
    // bind it.
    implementation(project(":feature:acquisition:domain"))
    implementation(libs.coil.compose)
}
