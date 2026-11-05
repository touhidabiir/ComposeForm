plugins {
    id("composeform.android.feature")
}

android {
    namespace = "com.touhid.composeform.feature.webview.ui"
}

dependencies {
    // Its own destination, to register the DestinationHandler that opens WebPageActivity.
    implementation(project(":feature:webview:navigation"))
}
