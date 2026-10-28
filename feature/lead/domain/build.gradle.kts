plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: both appear in this module's public types (AppResult in LeadRepository, LeadCloser/
    // Reviewer/Rejection in LeadListItem), so every consumer needs them too.
    api(project(":core:model"))
    api(project(":core:lead:domain"))
}
