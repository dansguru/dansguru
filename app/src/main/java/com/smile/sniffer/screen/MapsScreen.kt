package com.smile.sniffer.screen

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapsScreen(navController: NavController) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    // Initialize location provider
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L).build()

    var googleMap by remember { mutableStateOf<GoogleMap?>(null) }
    val userMarkerState = remember { mutableStateOf<Marker?>(null) }

    // Request location permission
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    // Request permission if not granted
    if (!locationPermissionState.status.isGranted) {
        LaunchedEffect(Unit) {
            locationPermissionState.launchPermissionRequest()
        }
    }

    // Initialize map and handle location updates
    AndroidView({ mapView }) { mapView ->
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap?.let { gMap ->
                if (locationPermissionState.status.isGranted) {
                    gMap.isMyLocationEnabled = true
                    // Start tracking location when map is ready
                    startTrackingLocation(fusedLocationClient, locationRequest, gMap, userMarkerState)
                }
            }
        }
    }
}

// Function to start tracking the user's location in real-time
private fun startTrackingLocation(
    fusedLocationClient: FusedLocationProviderClient,
    locationRequest: LocationRequest,
    googleMap: GoogleMap,
    userMarkerState: MutableState<Marker?>
) {
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location = locationResult.lastLocation ?: return
            val userLatLng = LatLng(location.latitude, location.longitude)

            // Remove previous marker if it exists
            userMarkerState.value?.remove()

            // Add new marker for user's location
            val newMarker = googleMap.addMarker(
                MarkerOptions().position(userLatLng).title("Current Location")
            )

            // Move and zoom the camera to the new location
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

            // Update the marker
            userMarkerState.value = newMarker
        }
    }

    // Start location updates
    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
}

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
    }
