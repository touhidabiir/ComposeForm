plugins {
    id("composeform.android.library.compose")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.touhid.composeform.formbuilder"
}

dependencies {
    implementation(project(":designsystem"))
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
