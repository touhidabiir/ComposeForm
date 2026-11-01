plugins {
    id("composeform.android.library")
}

android {
    namespace = "com.touhid.composeform.database"
    buildFeatures {
        // BuildConfig.DEBUG gates buildRoomDatabase's query logging.
        buildConfig = true
    }
}

dependencies {
    // api: safeDbCall returns AppResult, and buildRoomDatabase returns a RoomDatabase.Builder.
    api(project(":core:model"))
    api(libs.androidx.room.runtime)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
}
