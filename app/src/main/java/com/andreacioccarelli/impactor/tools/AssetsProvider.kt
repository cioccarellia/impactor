package com.andreacioccarelli.impactor.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import com.andreacioccarelli.impactor.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaredrummler.android.device.DeviceName
import com.jrummyapps.android.shell.Shell
import com.jrummyapps.android.shell.ShellExitCode
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

@SuppressLint("SetTextI18n")
class AssetsProvider : ShellExitCode {
    companion object {
        private fun err_ck1(v: ImageView) {
            v.setImageResource(R.drawable.check_error)
        }

        private fun err_ck2(v: ImageView) {
            v.setImageResource(R.drawable.hw_info_red)
        }


        private fun s1(v: ImageView) {
            v.setImageResource(R.drawable.check_ok)
        }

        private fun s2(v: ImageView) {
            v.setImageResource(R.drawable.hw_info_green)
        }


        private fun l1(v: ImageView) {
            v.setImageResource(R.drawable.loading)
        }

        private fun l2(v: ImageView) {
            v.setImageResource(R.drawable.loading)
        }

        fun init(
            ir: Boolean,
            bb: Boolean,
            i1: ImageView,
            i2: ImageView,
            c1: TextView,
            c2: TextView,
            fab: FloatingActionButton
        ) {
            CoroutineScope(Dispatchers.Main).launch {
                l1(i1)
                l2(i2)


                if (ir) {
                    s1(i1)
                    s2(i2)
                    c1.text = "Root access has been detected.\nImpactor ready to go"

                    fab.show()
                    // fab.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#00E676"))
                } else {
                    // Non-root detection is very fast.
                    // Delaying result display for a fraction of a second to make it feel like the app is
                    // doing something for users who don't have root access, and which probably downloaded the app without knowing anything,
                    // so they don't leave 1 star reviews complaining that the app is broken.
                    // Yes, I know, it's horrible, but it's not the worse thing about this
                    // class by a wide margin, and it's either that or I start cursing people out
                    delay(100L + Random.nextInt(50, 300))

                    err_ck1(i1)
                    err_ck2(i2)
                    c1.text = "Root access not installed or not allowed"
                    fab.hide()
                }


                c2.text =
                    "Device: " + DeviceName.getDeviceName() +
                            "\nAndroid Version: " + Build.VERSION.RELEASE + " (SDK ${Build.VERSION.SDK_INT})" +
                            // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {Build.VERSION.BASE_OS + ")"} else {")"} +
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                " (security patch ${Build.VERSION.SECURITY_PATCH})"
                            } else {
                                ""
                            } +
                            "\nBusybox: " + if (bb) "Installed" else "Not installed"
            }
        }
    }
}
