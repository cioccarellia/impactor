package com.andreacioccarelli.impactor.base

import android.content.Context
import android.os.Vibrator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.andreacioccarelli.impactor.tools.AssetsProvider
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jrummyapps.android.shell.Shell
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ImpactorActivity : AppCompatActivity() {

    fun vibrate(time: Int) {
        try {
            val v = baseContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            v.vibrate(time.toLong())
        } catch (ignored: Exception) {}
    }


    var root = false
    var busybox = false

    fun refreshRootLogic(
        i1: ImageView,
        i2: ImageView,
        c1: TextView,
        c2: TextView,
        fab: FloatingActionButton
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            root = false
            busybox = CodeExecutor().checkBusyBox()

            try {
                root = Shell.SU.available()
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toasty.error(this@ImpactorActivity, "Error raised while checking root", 1).show()
                }
            }

            AssetsProvider.init(root, busybox, i1, i2, c1, c2, fab)
        }
    }


}