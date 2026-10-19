package com.touhid.composeform.feature.acquisition.ui

import android.content.Context
import com.touhid.composeform.feature.acquisition.navigation.AcquisitionApprovalListDestination
import com.touhid.composeform.navigation.DestinationHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject

// Opens the acquisition destinations other modules navigate to. Only this module knows they're
// backed by AcquisitionActivity; callers only see AcquisitionApprovalListDestination.
internal class AcquisitionApprovalListHandler @Inject constructor() :
    DestinationHandler<AcquisitionApprovalListDestination> {
    override val type = AcquisitionApprovalListDestination::class
    override fun open(context: Context, destination: AcquisitionApprovalListDestination) {
        context.startActivity(AcquisitionActivity.intent(context))
    }
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class AcquisitionNavigationModule {

    @Binds
    @IntoSet
    abstract fun bindAcquisitionApprovalListHandler(handler: AcquisitionApprovalListHandler): DestinationHandler<*>
}
