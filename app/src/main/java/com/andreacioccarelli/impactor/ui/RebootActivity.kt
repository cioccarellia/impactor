package com.andreacioccarelli.impactor.ui

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.ImpactorActivity
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import com.google.android.material.navigation.NavigationView
import es.dmoral.toasty.Toasty

class RebootActivity : ImpactorActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reboot)

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
        val mCardReboot = findViewById<CardView>(R.id.card_reboot)
        val mCardShutdown = findViewById<CardView>(R.id.card_shutdown)
        val mCardRecovery = findViewById<CardView>(R.id.card_recovery)
        val mCardUI = findViewById<CardView>(R.id.card_ui)
        val mCardBoot = findViewById<CardView>(R.id.card_bootloader)
        val mCardSafe = findViewById<CardView>(R.id.card_safe)
        val mCardFastReboot = findViewById<CardView>(R.id.card_soft_reboot)

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
