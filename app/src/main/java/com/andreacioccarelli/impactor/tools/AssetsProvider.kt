package com.andreacioccarelli.impactor.tools

import android.annotation.SuppressLint
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import com.andreacioccarelli.impactor.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jaredrummler.android.device.DeviceName
import kotlinx.coroutines.delay
import kotlin.random.Random

@SuppressLint("SetTextI18n")
object AssetsProvider {

    /**
     * Paints the root/system check cards. Must be called on the main thread.
     */
    suspend fun render(
        isRooted: Boolean,
        hasBusybox: Boolean,
        rootIcon: ImageView,
        systemIcon: ImageView,
        rootText: TextView,
        systemText: TextView,
        fab: FloatingActionButton
    ) {
        rootIcon.setImageResource(R.drawable.loading)
        systemIcon.setImageResource(R.drawable.loading)

        if (isRooted) {
            rootIcon.setImageResource(R.drawable.check_ok)
            systemIcon.setImageResource(R.drawable.hw_info_green)
            rootText.text = "Root access has been detected.\nImpactor ready to go"
            fab.show()
        } else {
            // Non-root detection is very fast.
            // Delaying result display for a fraction of a second to make it feel like the app is
            // doing something for users who don't have root access, and which probably downloaded the app without knowing anything,
            // so they don't leave 1 star reviews complaining that the app is broken.
            // Yes, I know, it's horrible, but it's not the worse thing about this
            // class by a wide margin, and it's either that or I start cursing people out
            delay(100L + Random.nextInt(50, 300))

            rootIcon.setImageResource(R.drawable.check_error)
            systemIcon.setImageResource(R.drawable.hw_info_red)
            rootText.text = "Root access not installed or not allowed"
            fab.hide()
        }

        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            " (security patch ${Build.VERSION.SECURITY_PATCH})"
        } else {
            ""
        }

        systemText.text = "Device: " + DeviceName.getDeviceName() +
                "\nAndroid Version: " + Build.VERSION.RELEASE + " (SDK ${Build.VERSION.SDK_INT})" +
                securityPatch +
                "\nBusybox: " + if (hasBusybox) "Installed" else "Not installed"
    }
}
