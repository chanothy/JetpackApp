package com.example.jetpackapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Viewmodel for tasks view
 *
 * Has methods for instantiating the database and navigation control
 * Utilizes a system of listeners of live data for navigation.
 */

class SensorsViewModel : ViewModel() {

    private lateinit var temperatureSensor: MeasurableSensor
    private lateinit var pressureSensor: MeasurableSensor


    // sensor info
    var ambientTemperature: MutableLiveData<Float> = MutableLiveData()
    var humidity: MutableLiveData<Float> = MutableLiveData()

    fun initializeSensors(sTemperature: MeasurableSensor, sPressure: MeasurableSensor) {
        temperatureSensor = sTemperature
        pressureSensor = sPressure
        temperatureSensor.startListening()
        pressureSensor.startListening()
        temperatureSensor.setOnSensorValuesChangedListener { values ->
            ambientTemperature.value = values[0]
        }
        pressureSensor.setOnSensorValuesChangedListener { values ->
            humidity.value = values[0]
        }
    }
}