package com.touhid.composeform.feature.webview.ui

import android.content.Context
import com.touhid.composeform.feature.webview.navigation.WebPageDestination
import com.touhid.composeform.navigation.DestinationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Opens WebPageDestination. Only this module knows it's backed by WebPageActivity.
internal class WebPageHandler @Inject constructor() : DestinationHandler<WebPageDestination> {
    override val type = WebPageDestination::class
    override fun open(context: Context, destination: WebPageDestination) {
        context.startActivity(WebPageActivity.intent(context, destination.url, destination.title))
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class WebPageNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindWebPageHandler(handler: WebPageHandler): DestinationHandler<*>
}
