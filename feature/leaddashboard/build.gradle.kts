plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.leaddashboard"
}

dependencies {
    // Not :network directly - :data re-exports it via api() (see CLAUDE.md's Data layer notes),
    // so LeadListItem/NetworkResult/etc. are already resolvable through this one dependency. A
    // feature module has no reason to configure the network client (no @BaseUrl/TokenProvider
    // binding), so it never needs :network as a direct dependency the way :app does.
    implementation(project(":data"))
}
