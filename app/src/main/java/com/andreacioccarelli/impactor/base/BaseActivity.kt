package com.andreacioccarelli.impactor.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun vibrate(time: Int) {
        try {
            val v = baseContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(time.toLong())
        } catch (ignored: Exception) {}
    }
}