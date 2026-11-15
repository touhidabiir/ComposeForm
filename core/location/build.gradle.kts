plugins {
    // Compose: rememberLocationRequester runs the permission / turn-on-location dialogs.
    id("composeform.android.library.compose")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.location"
}

dependencies {
    // AppAlertDialog for the "why" and "open settings" dialogs.
    implementation(project(":core:designsystem"))
    // Fused location provider and the one-tap "turn on location" dialog.
    implementation(libs.google.play.services.location)
    // Task.await() for Play services calls.
    implementation(libs.kotlinx.coroutines.play.services)
    // Activity Result launchers and LocalActivity.
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
}
