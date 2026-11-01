plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.login.ui"

    buildFeatures {
        // BuildConfig.DEBUG: debug builds let the user type the OTP, release builds only fill it
        // from the SMS (OtpScreen).
        buildConfig = true
    }
}

dependencies {
    // Sign-in itself (SessionRepository, phone number rules) is the :core:session slice; this
    // feature is only its screens. :core:session:data is bound in :app for Hilt.
    implementation(project(":core:session:domain"))
    // Its own result contract, to bind the LoginIntentFactory it builds intents with.
    implementation(project(":feature:login:navigation"))
    // LoginActivity's NavHost (phone number -> OTP).
    implementation(libs.androidx.navigation.compose)
    // SMS Retriever: reads the OTP from the SMS without the SMS permission.
    implementation(libs.google.play.services.auth.api.phone)
    testImplementation(libs.junit)
}
