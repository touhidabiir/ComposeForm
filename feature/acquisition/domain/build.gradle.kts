plugins {
    id("composeform.jvm.library")
}

dependencies {
    // api: both appear in this module's public types (AppResult in AcquisitionRepository,
    // LeadCloser/Rejection in the acquisition models), so every consumer needs them too.
    api(project(":core:model"))
    api(project(":core:lead:domain"))
}
