package com.andreacioccarelli.impactor.tools

import android.app.Activity
import androidx.annotation.IdRes
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

object AdsUtil {
    fun initAds(activity: Activity, @IdRes id: Int) {
        MobileAds.initialize(activity) {}

        val adView = activity.findViewById(id) as AdView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}