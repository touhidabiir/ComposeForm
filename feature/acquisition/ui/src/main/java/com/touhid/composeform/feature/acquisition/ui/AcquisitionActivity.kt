package com.touhid.composeform.feature.acquisition.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.touhid.composeform.designsystem.theme.ComposeFormAppTheme
import dagger.hilt.android.AndroidEntryPoint

// The acquisition feature's host Activity: the entry point AcquisitionApprovalListHandler opens.
// Navigation between its own screens (list -> detail) stays inside this NavHost; other features
// never see these routes.
//
// The long @AndroidEntryPoint(ComponentActivity::class) form, extending the generated
// Hilt_AcquisitionActivity directly: only :app applies the Hilt Gradle plugin, and that plugin's
// bytecode rewrite is what lets the short form (`: ComponentActivity()`) work.
@AndroidEntryPoint(ComponentActivity::class)
class AcquisitionActivity : Hilt_AcquisitionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeFormAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = ListRoute) {
                    composable(ListRoute) { backStackEntry ->
                        // Set by the detail screen's onApprove/onReject just before popping back
                        // here - the list reacts by refreshing and showing a success snackbar,
                        // then clears it so it doesn't re-fire on a later recomposition.
                        val decisionResult by backStackEntry.savedStateHandle
                            .getStateFlow<String?>(DecisionResultKey, null)
                            .collectAsStateWithLifecycle()
                        AcquisitionApprovalListScreen(
                            onBack = ::finish,
                            onReview = { leadId -> navController.navigate("$DetailRoutePrefix/$leadId") },
                            decisionResult = decisionResult,
                            onDecisionResultConsumed = { backStackEntry.savedStateHandle[DecisionResultKey] = null },
                        )
                    }

                    // AcquisitionApprovalDetailViewModel reads the leadId argument from its
                    // SavedStateHandle by this name.
                    composable("$DetailRoutePrefix/{leadId}") {
                        AcquisitionApprovalDetailScreen(
                            onBack = { navController.popBackStack() },
                            onApprove = {
                                navController.previousBackStackEntry?.savedStateHandle?.set(DecisionResultKey, ReasonSheetType.Approve.name)
                                navController.popBackStack()
                            },
                            onReject = {
                                navController.previousBackStackEntry?.savedStateHandle?.set(DecisionResultKey, ReasonSheetType.Reject.name)
                                navController.popBackStack()
                            },
                        )
                    }
                }
            }
        }
    }

    companion object {
        private const val ListRoute = "acquisitionApprovalList"
        private const val DetailRoutePrefix = "acquisitionApprovalDetail"
        private const val DecisionResultKey = "acquisitionDecisionResult"

        fun intent(context: Context): Intent = Intent(context, AcquisitionActivity::class.java)
    }
}
