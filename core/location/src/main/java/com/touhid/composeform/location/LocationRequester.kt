package com.touhid.composeform.location

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.touhid.composeform.designsystem.components.surface.AppAlertDialog
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Gets the current location from a screen, handling everything in between, so no screen repeats
// the permission flow:
//   1. permission: ask; explain first if the user said no before; send to app settings if they
//      chose "Don't allow" for good;
//   2. location switched off: Play services' one-tap "turn on location" dialog;
//   3. LocationTracker.currentLocation().
// Every request ends with exactly one call to onResult.
//
// Asking for a permission needs an Activity on screen, which is why this is a composable and not
// part of LocationTracker. The usual wiring: the ViewModel decides a location is needed (state),
// the screen calls request(), the outcome goes back to the ViewModel:
//
//   val location = rememberLocationRequester(onResult = viewModel::onLocationResult)
//   LaunchedEffect(state.needsLocation) { if (state.needsLocation) { viewModel.onLocationRequestStarted(); location.request() } }
class LocationRequester internal constructor(private val start: () -> Unit) {
    fun request() = start()
}

@Composable
fun rememberLocationRequester(onResult: (LocationOutcome) -> Unit): LocationRequester {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val tracker = remember(context) {
        EntryPointAccessors.fromApplication(context.applicationContext, LocationEntryPoint::class.java).locationTracker()
    }
    val scope = rememberCoroutineScope()
    val currentOnResult by rememberUpdatedState(onResult)
    var dialog by remember { mutableStateOf<LocationDialog?>(null) }

    // 3. Permission granted and location on: get the position.
    fun fetch() {
        scope.launch { currentOnResult(tracker.currentLocation()) }
    }

    // 2. Location switched off: Play services' dialog switches it on in one tap.
    val turnOnLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) fetch() else currentOnResult(LocationOutcome.Disabled)
    }

    fun ensureEnabledThenFetch() {
        scope.launch {
            val request = LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L).build())
                .build()
            try {
                LocationServices.getSettingsClient(context).checkLocationSettings(request).await()
                fetch()
            } catch (e: CancellationException) {
                throw e
            } catch (e: ResolvableApiException) {
                turnOnLauncher.launch(IntentSenderRequest.Builder(e.resolution).build())
            } catch (e: Exception) {
                // Can't be fixed from here; let the tracker report what it can.
                fetch()
            }
        }
    }

    // 1. Permission.
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
        when {
            grants.values.any { it } -> ensureEnabledThenFetch()
            // Denied, and Android won't show its dialog again: only the app settings can help.
            activity != null && !activity.shouldShowRequestPermissionRationale(LocationPermissions.first()) ->
                dialog = LocationDialog.OpenSettings
            else -> currentOnResult(LocationOutcome.NoPermission)
        }
    }

    fun start() {
        when {
            hasLocationPermission(context) -> ensureEnabledThenFetch()
            // Said no before: explain why before asking again.
            activity != null && activity.shouldShowRequestPermissionRationale(LocationPermissions.first()) ->
                dialog = LocationDialog.Rationale
            else -> permissionLauncher.launch(LocationPermissions)
        }
    }

    when (dialog) {
        LocationDialog.Rationale -> AppAlertDialog(
            title = stringResource(R.string.location_rationale_title),
            message = stringResource(R.string.location_rationale_message),
            confirmText = stringResource(R.string.location_continue),
            onConfirm = {
                dialog = null
                permissionLauncher.launch(LocationPermissions)
            },
            onDismissRequest = {
                dialog = null
                currentOnResult(LocationOutcome.NoPermission)
            },
            dismissText = stringResource(R.string.location_not_now),
        )
        LocationDialog.OpenSettings -> AppAlertDialog(
            title = stringResource(R.string.location_blocked_title),
            message = stringResource(R.string.location_blocked_message),
            confirmText = stringResource(R.string.location_open_settings),
            onConfirm = {
                dialog = null
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
                // The user comes back on their own; the next request() finds the new permission.
                currentOnResult(LocationOutcome.NoPermission)
            },
            onDismissRequest = {
                dialog = null
                currentOnResult(LocationOutcome.NoPermission)
            },
            dismissText = stringResource(R.string.location_not_now),
        )
        null -> Unit
    }

    return remember { LocationRequester(::start) }
}

private enum class LocationDialog { Rationale, OpenSettings }
