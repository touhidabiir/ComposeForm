plugins {
    id("composeform.android.library")
}

android {
    namespace = "com.touhid.composeform.feature.webview.navigation"
}

dependencies {
    // api: WebPageDestination implements Destination, so callers need it too.
    api(project(":core:navigation"))
}
