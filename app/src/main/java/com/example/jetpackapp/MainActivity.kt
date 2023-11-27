package com.example.jetpackapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.jetpackapp.ui.theme.JetpackAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale


/**
 * Main Activity
 *
 * On app launch, it asks for all necessary permissions.
 * It checks for permissions before showing the location.
 */
class MainActivity : ComponentActivity() {
    private lateinit var sensorsViewModel: SensorsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorsViewModel = ViewModelProvider(this).get(SensorsViewModel::class.java)
        sensorsViewModel.initializeSensors(TemperatureSensor(applicationContext))


        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d("Permissions","")
                    setContent {
                        JetpackAppTheme {

                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                // Checks for permissions on coarse location
                                if (ActivityCompat.checkSelfPermission(
                                        this,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ) != PackageManager.PERMISSION_GRANTED
                                ) {
                                    Log.d("Current location","Permission Not Granted")
                                } else {
                                    // Shows info with SensorsView, gets info with getCoords
                                    SensorsView(getCoords().toString())
                                    Log.d("Coarse location","Permission Granted")
                                }
                            }
                        }
                    }
                } else -> {
                    Log.d("No Permissions", "")
                }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Function gets the location based on lat long
    private fun getLocation(context: Context, lat: Double, long: Double): String {
        val state: String?
        val city: String?
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat,long,3)

        state = address?.get(0)?.adminArea
        city = address?.get(0)?.locality
        return "City: $city\nState: $state"
    }

    private fun getTemp() {
    }

    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )

    @Composable
    private fun getCoords(): String? {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val locationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var locationInfo by remember {
            mutableStateOf("")
        }
        scope.launch(Dispatchers.IO) {
            val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val result = locationClient.getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            ).await()
            // if there is a result, use fetchedLocation and assign locationInfo
            result?.let { fetchedLocation ->
                locationInfo =
                    getLocation(context,
                        fetchedLocation.latitude, fetchedLocation.longitude)
            }
        }

        return locationInfo
    }


    // just a testing function to see how jetpack works
    @Composable
    fun ShowInfo(name: String, location: String, temperature: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Name: $name", style = TextStyle(fontSize = 24.sp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Location: $location", style = TextStyle(fontSize = 18.sp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Temperature: $temperature", style = TextStyle(fontSize = 18.sp))
        }
    }

    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )


    @Composable
    fun SensorsView(locationInfo: String) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val locationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
//    var locationInfo by remember {
//        mutableStateOf("")
//    }

        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sensors Playground\n",
                style = TextStyle(fontSize = 30.sp)
            )
            Text(
                text = "Tim Chan",
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = locationInfo,
                modifier = Modifier.align(Alignment.Start)
            )
        }
    }

    // For showing previews
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        JetpackAppTheme {
        }
    }
}




