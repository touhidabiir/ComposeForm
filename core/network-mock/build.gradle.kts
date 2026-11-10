plugins {
    id("composeform.android.library")
    id("composeform.hilt")
}

android {
    namespace = "com.touhid.composeform.network.mock"
}

// Mocked backend responses, kept out of :core:network so a build that doesn't want them leaves
// them out entirely: only :app depends on this module, and only for the flavors that may serve
// mocks (sitImplementation/uatImplementation in app/build.gradle.kts), so the prod APK contains
// none of this code. Whether the mocks are actually used is the flavor's BuildConfig.IS_MOCK_ENABLED.
dependencies {
    // For the @MockInterceptor qualifier this module provides its interceptor under.
    implementation(project(":core:network"))
    implementation(libs.squareup.okhttp)
}
