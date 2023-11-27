package com.example.jetpackapp

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

    // sensor info
    var ambientTemperature: MutableLiveData<Float> = MutableLiveData(0.00F)

    fun initializeSensors(sTemperature: MeasurableSensor) {
        //initialize light sensor
        temperatureSensor = sTemperature
        temperatureSensor.startListening()
        temperatureSensor.setOnSensorValuesChangedListener { values ->
            ambientTemperature.value = values[0]
        }

    }
}