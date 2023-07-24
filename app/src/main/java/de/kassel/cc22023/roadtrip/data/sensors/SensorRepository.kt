package de.kassel.cc22023.roadtrip.data.sensors

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.google.android.gms.location.FusedLocationProviderClient

class SensorRepository(
    private val locationProvider: FusedLocationProviderClient,
) : SensorEventListener {
    override fun onSensorChanged(p0: SensorEvent?) {

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}