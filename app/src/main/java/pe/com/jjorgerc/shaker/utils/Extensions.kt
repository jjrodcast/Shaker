package pe.com.jjorgerc.shaker.utils

import android.app.Activity
import android.support.v4.content.ContextCompat
import android.widget.TextView
import android.widget.Toast
import pe.com.jjorgerc.shaker.R
import kotlin.random.Random

private val colors = arrayOf(
    R.color.color1,
    R.color.color2,
    R.color.color3,
    R.color.color4,
    R.color.color5,
    R.color.color6,
    R.color.color7,
    R.color.color8,
    R.color.color9,
    R.color.color10
)


fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()


fun TextView.changeColor() {
    val pos = Random.nextInt(10)
    setTextColor(ContextCompat.getColor(context, colors[pos]))
}