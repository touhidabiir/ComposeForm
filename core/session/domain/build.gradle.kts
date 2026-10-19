plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: AppResult appears in SessionRepository's public signature.
    api(project(":core:model"))
}
