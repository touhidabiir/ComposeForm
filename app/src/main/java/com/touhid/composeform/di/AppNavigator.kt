package com.touhid.composeform.di

import android.content.Context
import com.touhid.composeform.navigation.Destination
import com.touhid.composeform.navigation.DestinationHandler
import com.touhid.composeform.navigation.Navigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

// Dispatches each Destination to the DestinationHandler its owning feature registered (via
// @Binds @IntoSet in that feature's ui module). :app never knows what a destination opens.
// Bound in ActivityComponent so handlers start Activities from a real Activity context, not the
// Application one.
class AppNavigator @Inject constructor(
    @ActivityContext private val context: Context,
    private val handlers: Set<@JvmSuppressWildcards DestinationHandler<*>>,
) : Navigator {

    override fun navigate(destination: Destination) {
        val handler = handlers.firstOrNull { it.type.java.isInstance(destination) }
            ?: error(
                "No DestinationHandler for ${destination.javaClass.name}. Its feature's ui module " +
                    "registers one; is that module a dependency of :app?",
            )
        // Safe: the handler was picked because its type matches this destination.
        @Suppress("UNCHECKED_CAST")
        (handler as DestinationHandler<Destination>).open(context, destination)
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class AppNavigationModule {

    @Binds
    abstract fun bindNavigator(impl: AppNavigator): Navigator
}
