package com.andreacioccarelli.impactor.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.cardview.widget.CardView
import androidx.appcompat.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.afollestad.assent.Assent
import com.afollestad.assent.PermissionResultSet
import com.andreacioccarelli.impactor.tools.Core
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.BaseActivity
import com.andreacioccarelli.impactor.tools.AdsUtil
import com.andreacioccarelli.impactor.tools.CodeExecutor

import es.dmoral.toasty.Toasty

class RebootActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reboot)


        AdsUtil.initAds(this, R.id.adView)

        val warningPermissionError = findViewById<androidx.cardview.widget.CardView>(R.id.ErrorPermissionCard)

        Thread {
            warningPermissionError.setOnClickListener { view ->

                val packageName = packageName
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                }
            }
        }.start()

        Assent.setActivity(this@RebootActivity, this@RebootActivity)
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            warningPermissionError.visibility = View.VISIBLE
            Assent.requestPermissions({ result: PermissionResultSet ->
                if (result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    warningPermissionError.visibility = View.GONE
                } else if (!result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    warningPermissionError.visibility = View.VISIBLE
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE)
        } else {
            warningPermissionError.visibility = View.GONE
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this@RebootActivity, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        val mExecutor = CodeExecutor()
        val mCardReboot = findViewById<androidx.cardview.widget.CardView>(R.id.card_reboot)
        val mCardShutdown = findViewById<androidx.cardview.widget.CardView>(R.id.card_shutdown)
        val mCardRecovery = findViewById<androidx.cardview.widget.CardView>(R.id.card_recovery)
        val mCardUI = findViewById<androidx.cardview.widget.CardView>(R.id.card_ui)
        val mCardBoot = findViewById<androidx.cardview.widget.CardView>(R.id.card_bootloader)
        val mCardSafe = findViewById<androidx.cardview.widget.CardView>(R.id.card_safe)
        val mCardFastReboot = findViewById<androidx.cardview.widget.CardView>(R.id.card_soft_reboot)

        mCardReboot.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.Reboot).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while rebooting device", Toast.LENGTH_SHORT).show()
        }

        mCardShutdown.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.Shutdown).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while powering off device", Toast.LENGTH_SHORT).show()
        }

        mCardRecovery.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.RebootRecovery).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while rebooting device", Toast.LENGTH_SHORT).show()
        }

        mCardUI.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.RestartUI).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while restarting UI", Toast.LENGTH_SHORT).show()
        }

        mCardBoot.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.RebootBootloader).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while rebooting device", Toast.LENGTH_SHORT).show()
        }

        mCardSafe.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.RebootSafemode).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while rebooting device", Toast.LENGTH_SHORT).show()
        }

        mCardFastReboot.setOnClickListener { view ->
            if (mExecutor.execAsRoot(Core.reboot.FastReboot).exitCode != 0)
                Toasty.error(this@RebootActivity, "Error while rebooting device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        Assent.setActivity(this@RebootActivity, this@RebootActivity)
        val warningPermissionError = findViewById<androidx.cardview.widget.CardView>(R.id.ErrorPermissionCard)

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            warningPermissionError.visibility = View.VISIBLE
        } else {
            warningPermissionError.visibility = View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) Assent.setActivity(this@RebootActivity, this@RebootActivity)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Assent.handleResult(permissions, grantResults)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            if (!drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.openDrawer(GravityCompat.START)
                vibrate(10)
            } else if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
                vibrate(10)
            }
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.main_menu) {
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawer.openDrawer(GravityCompat.START)
            return true
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.nav_impactor) {
            val impactor = Intent(this@RebootActivity, CompleteUnrootActivity::class.java)
            startActivity(impactor)
        } else if (id == R.id.nav_erase) {
            val erase = Intent(this@RebootActivity, WipeActivity::class.java)
            startActivity(erase)
        } else if (id == R.id.nav_unroot) {
            val unroot = Intent(this@RebootActivity, UnrootActivity::class.java)
            startActivity(unroot)
        } else if (id == R.id.nav_info) {
            val info = Intent(this@RebootActivity, AboutActivity::class.java)
            startActivity(info)
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
