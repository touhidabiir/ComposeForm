package com.touhid.composeform.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import kotlin.time.Duration

// LocationTracker backed by Google Play's fused location provider, which mixes GPS, Wi-Fi, cell
// and sensors and is usually faster and lighter on battery than picking a provider by hand.
internal class FusedLocationTracker @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : LocationTracker {

    private val client by lazy { LocationServices.getFusedLocationProviderClient(context) }

    override fun hasPermission(): Boolean = hasLocationPermission(context)

    private fun isLocationEnabled(): Boolean =
        context.getSystemService(LocationManager::class.java)
            ?.let(LocationManagerCompat::isLocationEnabled) ?: false

    @SuppressLint("MissingPermission") // checked by hasPermission() first
    override suspend fun currentLocation(timeout: Duration): LocationOutcome {
        if (!hasPermission()) return LocationOutcome.NoPermission
        if (!isLocationEnabled()) return LocationOutcome.Disabled
        return try {
            val fresh: Location? = withTimeoutOrNull(timeout) {
                val cancel = CancellationTokenSource()
                // await(cancel): cancelling this coroutine cancels the GPS request too.
                client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancel.token).await(cancel)
            }
            // No fresh position in time: the last known one is better than nothing.
            val location: Location? = fresh ?: client.lastLocation.await()
            location?.let { LocationOutcome.Found(it.toGeoPoint()) } ?: LocationOutcome.Unavailable
        } catch (e: CancellationException) {
            throw e
        } catch (e: SecurityException) {
            // Permission revoked between the check and the request.
            LocationOutcome.NoPermission
        } catch (e: Exception) {
            LocationOutcome.Unavailable
        }
    }

    @SuppressLint("MissingPermission") // checked below
    override fun updates(interval: Duration): Flow<GeoPoint> = callbackFlow {
        if (!hasPermission()) {
            close(SecurityException("No location permission"))
            return@callbackFlow
        }
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval.inWholeMilliseconds).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it.toGeoPoint()) }
            }
        }
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())
        // Collection stopped (screen gone, scope cancelled): stop the updates and the GPS with them.
        awaitClose { client.removeLocationUpdates(callback) }
    }

    private fun Location.toGeoPoint() = GeoPoint(
        latitude = latitude,
        longitude = longitude,
        accuracyMeters = if (hasAccuracy()) accuracy else null,
    )
}

internal val LocationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

// Fine or coarse is enough: coarse still gives a position, just less precise.
internal fun hasLocationPermission(context: Context): Boolean =
    LocationPermissions.any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
