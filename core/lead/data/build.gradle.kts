plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: the mappers return these domain types, so every consumer needs them too.
    api(project(":core:lead:domain"))
    // Only for @SerializedName on the DTOs; the feature data modules that parse them declare
    // Gson themselves.
    implementation(libs.google.gson)
}
