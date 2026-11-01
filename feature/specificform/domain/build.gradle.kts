plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: AppResult and Flow appear in SpecificFormRepository's public signature.
    api(project(":core:model"))
    api(libs.kotlinx.coroutines.core)
}
