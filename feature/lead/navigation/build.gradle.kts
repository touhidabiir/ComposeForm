plugins {
    id("composeform.android.library")
}

android {
    namespace = "com.touhid.composeform.feature.lead.navigation"
}

dependencies {
    // api: every destination here implements Destination, so callers need it too.
    api(project(":core:navigation"))
}
