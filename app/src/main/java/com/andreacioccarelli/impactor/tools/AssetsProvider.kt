package com.andreacioccarelli.impactor.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.widget.ImageView
import android.widget.TextView

import com.andreacioccarelli.impactor.R
import com.jaredrummler.android.device.DeviceName
import com.jrummyapps.android.shell.Shell
import com.jrummyapps.android.shell.ShellExitCode

import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("SetTextI18n")
class AssetsProvider : ShellExitCode {
    companion object {
        fun err_ck1(v: ImageView) {
            v.setImageResource(R.drawable.check_error)
        }

        fun err_ck2(v: ImageView) {
            v.setImageResource(R.drawable.hw_info_red)
        }

        fun s1(v: ImageView) {
            v.setImageResource(R.drawable.check_ok)
        }

        fun s2(v: ImageView) {
            v.setImageResource(R.drawable.hw_info_green)
        }

        fun init(ir: Boolean, i1: ImageView, i2: ImageView, c1: TextView, c2: TextView) {
            if (ir) {
                s1(i1)
                s2(i2)
                c1.text = "Root access has been detected, Impactor is ready to go"
                c2.text = "Device: " + DeviceName.getDeviceName() + "\nAndroid Version: " + Build.VERSION.RELEASE + "\nBusybox: " + if (CodeExecutor().checkBusyBox()) "Installed" else "Not installed"
            } else {
                err_ck1(i1)
                err_ck2(i2)
                c1.text = "Root access not installed or not allowed, Impactor cannot work on this device"
                c2.text = "Device: " + DeviceName.getDeviceName() + "\nAndroid Version: " + Build.VERSION.RELEASE + "\nRoot: Not available"
            }
        }

        fun init(ctx: Context, i1: ImageView, i2: ImageView, c1: TextView, c2: TextView) {

            CoroutineScope(Dispatchers.IO).launch {
                var root = false
                val busybox = CodeExecutor().checkBusyBox()

                try {
                    root = Shell.SU.available()
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toasty.error(ctx, "Error raised while checking root", 1).show()
                    }
                }

                withContext(Dispatchers.Main) {
                    if (root) {
                        s1(i1)
                        s2(i2)
                        c1.text = "Root access has been detected, Impactor is ready to go"
                        c2.text = "Device: " + DeviceName.getDeviceName() + "\nAndroid Version: " + Build.VERSION.RELEASE + "\nBusybox: " + if (busybox) "Installed" else "Not installed"
                    } else {
                        err_ck1(i1)
                        err_ck2(i2)
                        c1.text = "Root access not installed or not allowed, Impactor cannot work on this device"
                        c2.text = "Device: " + DeviceName.getDeviceName() + "\nAndroid Version: " + Build.VERSION.RELEASE + "\nRoot: Not available"
                    }
                }

                PreferenceBuilder(ctx, PreferenceBuilder.DefaultFilename).putBoolean("root", root)
            }
        }
    }
}
