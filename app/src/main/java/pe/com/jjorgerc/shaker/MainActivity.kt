package pe.com.jjorgerc.shaker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pe.com.jjorgerc.shaker.utils.changeColor
import pe.com.jjorgerc.shaker.utils.toast
import kotlin.math.abs
import kotlin.math.max

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var manager: SensorManager
    private val gravity = arrayOf(0f, 0f, 0f)
    private var beginTime = 0L
    private var lasAcceleration = 0f
    private val intervalTime = 100L
    val GAP = 3f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createSensorManager()

        beginTime = System.currentTimeMillis()
    }

    override fun onResume() {
        super.onResume()
        registerSensorManager()
    }

    override fun onPause() {
        super.onPause()
        unregisterSensorManager()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val accelaration = getAcceleration(event.values)
            if (accelaration > GAP && shakeDifference(accelaration)) {
                lasAcceleration = accelaration
                message.changeColor()
            }
        } else toast(getString(R.string.sensor_message))
    }

    override fun onAccuracyChanged(sensor: Sensor?, acurracy: Int) = Unit

    private fun createSensorManager() {
        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private fun registerSensorManager() {
        val sensor = getAccelerometerSensor()
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun unregisterSensorManager() {
        manager.unregisterListener(this)
    }

    private fun getAccelerometerSensor(): Sensor {
        return manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun shakeDifference(acceleration: Float) = abs(acceleration - lasAcceleration) > 3

    private fun getAcceleration(values: FloatArray): Float {
        val alpha = 0.8f
        val accelaration = calculateAcceleration(values, alpha)

        val currentTime = System.currentTimeMillis()
        if (currentTime - beginTime > intervalTime) {
            beginTime = currentTime
            return accelaration
        }
        return 0f
    }

    private fun calculateAcceleration(values: FloatArray, alpha: Float): Float {
        val gravities = calculateGravitiy(values, alpha)

        val x = values[0] - gravities[0]
        val y = values[1] - gravities[1]
        val z = values[2] - gravities[2]

        return max(max(x, y), z)
    }

    private fun calculateGravitiy(value: FloatArray, alpha: Float): Array<Float> {
        gravity[0] = lowPassGravity(alpha, value[0], gravity[0])
        gravity[1] = lowPassGravity(alpha, value[1], gravity[1])
        gravity[2] = lowPassGravity(alpha, value[2], gravity[0])

        return gravity
    }

    private fun lowPassGravity(alpha: Float, value: Float, gravity: Float): Float {
        return alpha * gravity + (1 - alpha) * value
    }
}
