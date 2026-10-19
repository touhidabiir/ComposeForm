plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.lead.ui"
}

dependencies {
    // The ui layer sees only the lead domain (models + LeadRepository interface). The
    // implementation lives in :feature:lead:data, which :app depends on so Hilt can bind it.
    implementation(project(":feature:lead:domain"))
    // Its own destinations, to register a DestinationHandler for each.
    implementation(project(":feature:lead:navigation"))
}
