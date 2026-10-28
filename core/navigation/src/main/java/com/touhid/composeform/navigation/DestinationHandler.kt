package com.touhid.composeform.navigation

import android.content.Context
import kotlin.reflect.KClass

// Knows how to open one Destination type. Each feature's `ui` module implements one per
// destination it owns and registers it in a Hilt module with @Binds @IntoSet, so :app's Navigator
// can find it without :app knowing what the destination opens.
interface DestinationHandler<D : Destination> {
    val type: KClass<D>
    fun open(context: Context, destination: D)
}
