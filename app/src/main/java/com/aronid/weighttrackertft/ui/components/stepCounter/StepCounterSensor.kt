package com.aronid.weighttrackertft.ui.components.stepCounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*


class StepCounterSensor(
    context: Context,
    private val onStepCountChanged: (Int) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = null

    fun start() {
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val steps = it.values[0].toInt()
            onStepCountChanged(steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}


class StepCounterManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("step_counter_prefs", Context.MODE_PRIVATE)

    private val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    fun getStepsForToday(currentSteps: Int): Int {
        val todayKey = dateFormat.format(Date())

        val savedDate = prefs.getString("lastDate", null)
        val baseSteps = prefs.getInt("baseSteps", currentSteps)

        // Si es un nuevo día, actualizamos la base
        if (savedDate != todayKey) {
            prefs.edit()
                .putString("lastDate", todayKey)
                .putInt("baseSteps", currentSteps)
                .apply()
            return 0
        }

        return currentSteps - baseSteps
    }
}
