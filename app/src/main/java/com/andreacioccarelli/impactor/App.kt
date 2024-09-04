package com.andreacioccarelli.impactor

import android.app.Application
import com.jaredrummler.android.device.DeviceName

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        DeviceName.init(this)
    }

}