plugins {
    id("composeform.android.library")
    id("composeform.hilt")
    // :core:network, Retrofit and Gson, all implementation-only.
    id("composeform.android.retrofit")
    // Room for the saved submissions, plus :core:database (buildRoomDatabase, safeDbCall).
    id("composeform.android.room")
}

android {
    namespace = "com.touhid.composeform.feature.specificform.data"
}

dependencies {
    implementation(project(":feature:specificform:domain"))
}
