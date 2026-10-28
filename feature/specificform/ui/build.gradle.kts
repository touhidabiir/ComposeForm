plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.specificform.ui"
}

dependencies {
    // The ui layer sees only the specificform domain (model + SpecificFormRepository interface).
    // The implementation lives in :feature:specificform:data, which :app depends on so Hilt can
    // bind it.
    implementation(project(":feature:specificform:domain"))
    implementation(project(":formbuilder"))
    // FormSchema/FormField from :formbuilder are @Serializable; their generated members reference
    // kotlinx.serialization types, so the compiler needs them resolvable here too.
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.fragment.ktx)
}
