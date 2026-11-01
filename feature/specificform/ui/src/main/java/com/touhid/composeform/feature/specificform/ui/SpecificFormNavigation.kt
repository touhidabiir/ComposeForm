package com.touhid.composeform.feature.specificform.ui

import android.content.Context
import com.touhid.composeform.feature.specificform.navigation.SpecificFormDestination
import com.touhid.composeform.navigation.DestinationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Opens the specific-form destinations other modules navigate to. Only this module knows they're
// backed by SpecificFormActivity; callers only see SpecificFormDestination.
internal class SpecificFormHandler @Inject constructor() : DestinationHandler<SpecificFormDestination> {
    override val type = SpecificFormDestination::class
    override fun open(context: Context, destination: SpecificFormDestination) {
        context.startActivity(SpecificFormActivity.intent(context))
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SpecificFormNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindSpecificFormHandler(handler: SpecificFormHandler): DestinationHandler<*>
}
