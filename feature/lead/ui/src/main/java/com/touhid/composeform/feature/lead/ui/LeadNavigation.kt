package com.touhid.composeform.feature.lead.ui

import android.content.Context
import com.touhid.composeform.feature.lead.navigation.LeadDashboardDestination
import com.touhid.composeform.navigation.DestinationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Opens the lead destinations other modules navigate to. Only this module knows they're backed by
// LeadActivity; callers only see LeadDashboardDestination.
internal class LeadDashboardHandler @Inject constructor() : DestinationHandler<LeadDashboardDestination> {
    override val type = LeadDashboardDestination::class
    override fun open(context: Context, destination: LeadDashboardDestination) {
        context.startActivity(LeadActivity.intent(context))
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LeadNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindLeadDashboardHandler(handler: LeadDashboardHandler): DestinationHandler<*>
}
