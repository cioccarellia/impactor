package com.andreacioccarelli.impactor.base

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.afollestad.assent.Permission
import com.afollestad.assent.askForPermissions
import com.afollestad.assent.isAllGranted
import com.google.android.material.floatingactionbutton.FloatingActionButton

open class BaseActivity : AppCompatActivity() {

    fun vibrate(time: Int) {
        try {
            val v = baseContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(time.toLong())
        } catch (ignored: Exception) {}
    }

    fun checkPermissions(warningView: CardView, fab: FloatingActionButton?) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {

            if (!isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
                warningView.visibility = View.VISIBLE

                askForPermissions(Permission.WRITE_EXTERNAL_STORAGE) { result ->
                    if (result.isAllGranted(Permission.WRITE_EXTERNAL_STORAGE)) {
                        warningView.visibility = View.GONE
                        fab?.show()
                    } else {
                        warningView.visibility = View.VISIBLE
                    }
                }
            } else {
                warningView.visibility = View.GONE
            }
        } else {
            warningView.visibility = View.GONE
            fab!!.show()
        }
    }

}