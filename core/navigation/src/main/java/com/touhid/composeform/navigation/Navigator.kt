package com.touhid.composeform.navigation

// Opens a Destination owned by any feature. :app implements it by dispatching to the
// DestinationHandler registered for the destination's type.
//
// ViewModels never call this: they emit a one-off event, and the Activity, Fragment or
// composable that observes it calls navigate().
interface Navigator {
    fun navigate(destination: Destination)
}
