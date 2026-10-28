package com.touhid.composeform.feature.acquisition.navigation

import com.touhid.composeform.navigation.Destination

// The acquisition screens other modules may open, through Navigator. The only acquisition module
// anything outside the acquisition feature may depend on. The detail screen isn't here: only the
// list opens it, inside the feature's own NavHost.

data object AcquisitionApprovalListDestination : Destination
