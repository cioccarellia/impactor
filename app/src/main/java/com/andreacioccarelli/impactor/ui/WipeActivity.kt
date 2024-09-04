package com.andreacioccarelli.impactor.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.ImpactorActivity
import com.andreacioccarelli.impactor.tools.AssetsProvider
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.jrummyapps.android.shell.Shell
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WipeActivity : ImpactorActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.erase)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this@WipeActivity, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        assert(drawer != null)
        drawer!!.setDrawerListener(toggle)
        toggle.syncState()


        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView?.setNavigationItemSelectedListener(this)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            if (root) {
                MaterialDialog.Builder(this@WipeActivity)
                        .title(R.string.DialogEraseTitle)
                        .content(R.string.DialogEraseContent)
                        .positiveText(R.string.DialogContinue)
                        .negativeText(R.string.DialogCancel)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.DeepOrange_500)
                        .onPositive { _, _ ->
                            if (root) {
                                val unrootDialog = MaterialDialog.Builder(this@WipeActivity)
                                        .title(R.string.ProgressDialogWipeTitle)
                                        .content(getString(R.string.initializing_environment))
                                        .cancelable(false)
                                        .progress(true, 0)
                                        .progressIndeterminateStyle(false)
                                        .backgroundColorRes(R.color.Grey_800)
                                        .show()


                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(300)
                                    unrootDialog.setContent(getString(R.string.progress_wipe))
                                }

                                CoroutineScope(Dispatchers.IO).launch {
                                    executor.exec(Core.misc.init)
                                    executor.exec(Core.misc.mountRW)
                                    executor.exec(Core.unroot.erase_data_root)
                                    executor.autoReboot()
                                }
                                //executor.execQueue();

                            }


                        }
                        .onNegative { dialog, which -> dialog.dismiss() }
                        .show()
            } else {
                MaterialDialog.Builder(this@WipeActivity)
                        .title(getString(R.string.continue_in_system_question))
                        .content(getString(R.string.continue_in_system_content))
                        .positiveText(getString(R.string.continue_in_settings_positive))
                        .negativeText(getString(R.string.continue_in_settings_negative))
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.Red_500)
                        .autoDismiss(true)
                        .onPositive { dialog, which -> startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        .onNegative { dialog, which -> dialog.dismiss() }
                        .show()
            }
        }

    }


    override fun onResume() {
        super.onResume()

        val i1: ImageView = findViewById(R.id.check_image)
        val i2: ImageView = findViewById(R.id.hw_check_image)
        val c1: TextView = findViewById(R.id.check_text)
        val c2: TextView = findViewById(R.id.hw_check_text)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        refreshRootLogic(i1, i2, c1, c2, fab)
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
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)!!
            drawer.openDrawer(GravityCompat.START)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_impactor) {
            val impactor = Intent(this@WipeActivity, CompleteUnrootActivity::class.java)
            startActivity(impactor)
        } else if (id == R.id.nav_unroot) {
            val unroot = Intent(this@WipeActivity, UnrootActivity::class.java)
            startActivity(unroot)
        } else if (id == R.id.nav_reboot) {
            val reboot = Intent(this@WipeActivity, RebootActivity::class.java)
            startActivity(reboot)
        } else if (id == R.id.nav_info) {
            val info = Intent(this@WipeActivity, AboutActivity::class.java)
            startActivity(info)
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)!!
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}