package com.java.orientation

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.widget.Toast

class OrientationService : Service(), SensorEventListener {

    private lateinit var rotationSensorData: Sensor
    private lateinit var sensorManager : SensorManager
    private val sensorDataInterval = 8 // in millisecond
    var orientationSensorData = IntArray(3) { 0 }
    private var clientBindCount = 0
    val myLock = Any()

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationSensorData = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    }

    @Synchronized override fun onBind(intent: Intent): IBinder {
        synchronized(myLock) {
            clientBindCount++
            if (clientBindCount >= 1) {
                startSensorListener()
            }
        }

        return object : IOrientationInterface.Stub() {
            override fun getOrientationData(): IntArray {
                return orientationSensorData
            }
        }
    }

    @Synchronized override fun onUnbind(intent: Intent?): Boolean {
        synchronized(myLock) {
            clientBindCount--
            if (clientBindCount == 0) {
                stopSensorListener()
            }
        }
        return super.onUnbind(intent)
    }

    private fun startSensorListener() {
        try {
            sensorManager.registerListener(this, rotationSensorData, sensorDataInterval)
        } catch (e: Exception) {
            Toast.makeText(this, "Sensor is not supported on this HW", Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun stopSensorListener() {
        try {
            sensorManager.unregisterListener(this, rotationSensorData)
        } catch (e: Exception) {
            Toast.makeText(this, "Sensor is not supported on this HW", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        //if the sensor is unreliable, return void
        if (event!!.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }

        if (event.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                val updatedRotationReading = FloatArray(3)
                System.arraycopy(
                    event.values, 0, updatedRotationReading,
                    0, updatedRotationReading.size
                )
                getSensorValue(updatedRotationReading)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun getSensorValue(vectors: FloatArray) {
        val rotationSensorMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationSensorMatrix, vectors)
        SensorManager
            .remapCoordinateSystem(rotationSensorMatrix,
                SensorManager.AXIS_X, SensorManager.AXIS_Z,
                rotationSensorMatrix)
        val orientationValues = FloatArray(3)
        SensorManager.getOrientation(rotationSensorMatrix, orientationValues)

        orientationSensorData[0] = Math.toDegrees(orientationValues[0].toDouble()).toInt()
        orientationSensorData[1] = Math.toDegrees(orientationValues[1].toDouble()).toInt()
        orientationSensorData[2] = Math.toDegrees(orientationValues[2].toDouble()).toInt()

    }
}