plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: AppResult appears in LoginRepository's public signature.
    api(project(":core:model"))
    testImplementation(libs.junit)
}
