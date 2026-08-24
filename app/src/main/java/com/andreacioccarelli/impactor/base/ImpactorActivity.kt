package com.andreacioccarelli.impactor.base

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.tools.AssetsProvider
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.ui.AboutActivity
import com.andreacioccarelli.impactor.ui.CompleteUnrootActivity
import com.andreacioccarelli.impactor.ui.RebootActivity
import com.andreacioccarelli.impactor.ui.UnrootActivity
import com.andreacioccarelli.impactor.ui.WipeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.jrummyapps.android.shell.Shell
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class ImpactorActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var root = false
    var busybox = false

    private val drawer: DrawerLayout?
        get() = findViewById(R.id.drawer_layout)

    /**
     * Back closes the drawer instead of leaving the app. Enabled only while the drawer is
     * open, so an ordinary back press still exits.
     *
     * Overriding onBackPressed() no longer works: apps targeting API 35+ get predictive back,
     * and the platform routes back through OnBackPressedDispatcher instead.
     */
    private val closeDrawerOnBack = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            drawer?.closeDrawer(GravityCompat.START)
        }
    }

    /**
     * Wires up the toolbar, the drawer toggle and the navigation drawer. Every screen in the
     * app shares the same drawer, so the whole thing lives here rather than being repeated
     * verbatim in each activity.
     */
    protected fun setupDrawer(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        val drawer = this.drawer ?: return
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: android.view.View) {
                closeDrawerOnBack.isEnabled = true
            }

            override fun onDrawerClosed(drawerView: android.view.View) {
                closeDrawerOnBack.isEnabled = false
            }
        })
        onBackPressedDispatcher.addCallback(this, closeDrawerOnBack)

        findViewById<NavigationView>(R.id.nav_view)?.setNavigationItemSelectedListener(this)
    }

    fun vibrate(time: Int) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(time.toLong(), VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(time.toLong())
            }
        } catch (ignored: Exception) {}
    }

    fun refreshRootLogic(
        i1: ImageView,
        i2: ImageView,
        c1: TextView,
        c2: TextView,
        fab: FloatingActionButton
    ) {
        // lifecycleScope, so a rotation or a config change cancels the check instead of
        // leaving it writing into the views of a destroyed activity. Android 16 ignores the
        // portrait lock on large screens, so config changes now actually happen here.
        lifecycleScope.launch {
            root = false

            withContext(Dispatchers.IO) {
                busybox = CodeExecutor().checkBusyBox()

                try {
                    root = Shell.SU.available()
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        Toasty.error(this@ImpactorActivity, "Error raised while checking root", 1).show()
                    }
                }
            }

            AssetsProvider.render(root, busybox, i1, i2, c1, c2, fab)
        }
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        val drawer = this.drawer
        if (keyCode == KeyEvent.KEYCODE_BACK && drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START)
            } else {
                drawer.openDrawer(GravityCompat.START)
            }
            vibrate(10)
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_menu) {
            drawer?.openDrawer(GravityCompat.START)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destination = when (item.itemId) {
            R.id.nav_impactor -> CompleteUnrootActivity::class.java
            R.id.nav_erase -> WipeActivity::class.java
            R.id.nav_unroot -> UnrootActivity::class.java
            R.id.nav_reboot -> RebootActivity::class.java
            R.id.nav_info -> AboutActivity::class.java
            else -> null
        }

        // Never re-launch the screen we are already on.
        if (destination != null && destination != this::class.java) {
            startActivity(Intent(this, destination))
        }

        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
