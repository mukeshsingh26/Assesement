package com.java.application

import android.app.Activity
import android.app.Service
import android.content.*
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.java.orientation.BuildConfig
import com.java.orientation.IOrientationInterface
import com.java.orientation.OrientationService
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private var orientationInterface: IOrientationInterface? = null

    private var mSensorDataHandler: Handler? = null
    private var mSensorDataThread: HandlerThread? = null

    private val uiRefreshInterval = 1000L // in millisecond

    var yawValue: TextView? = null
    var pitchValue: TextView? = null
    var rollValue: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        yawValue = findViewById(R.id.yaw_value)
        pitchValue = findViewById(R.id.pitch_value)
        rollValue = findViewById(R.id.roll_value)

        val mSensorDataThreadName = "SensorData"
        mSensorDataThread = HandlerThread(mSensorDataThreadName)
        mSensorDataThread!!.start()
        mSensorDataHandler = Handler(mSensorDataThread!!.looper)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, OrientationService::class.java)
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE)

        mSensorDataHandler?.post(sensorDataRunnable)

        updateSensorData()
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            orientationInterface = IOrientationInterface.Stub.asInterface(service)
        }

        override fun onServiceDisconnected(name: ComponentName) {
            orientationInterface = null
        }
    }

    private val sensorDataRunnable: Runnable = object : Runnable {

        override fun run() {
            val orientation = orientationInterface?.orientationData

            if (BuildConfig.DEBUG) Log.d(TAG, "Yaw : " + orientation?.get(0)?.toString())
            if (BuildConfig.DEBUG) Log.d(TAG, "Pitch : " + orientation?.get(1)?.toString())
            if (BuildConfig.DEBUG) Log.d(TAG, "roll : " + orientation?.get(2)?.toString())
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post {
                // Your UI updates here
                yawValue?.text = orientation?.get(0)?.toString()
                pitchValue?.text = orientation?.get(1)?.toString()
                rollValue?.text = orientation?.get(2)?.toString()
            }

            mSensorDataHandler?.postDelayed(this, uiRefreshInterval)


        }
    }


    private fun updateSensorData() {
        mSensorDataHandler?.postDelayed(sensorDataRunnable, uiRefreshInterval)
    }
}