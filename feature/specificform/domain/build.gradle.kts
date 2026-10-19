plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: AppResult appears in SpecificFormRepository's public signature.
    api(project(":core:model"))
}
