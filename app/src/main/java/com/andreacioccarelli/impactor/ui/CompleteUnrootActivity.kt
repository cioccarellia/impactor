package com.andreacioccarelli.impactor.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
import android.widget.ImageView
import android.widget.TextView
import com.afollestad.assent.Assent
import com.afollestad.assent.PermissionResultSet
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.BuildConfig
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.BaseActivity
import com.andreacioccarelli.impactor.tools.*

class CompleteUnrootActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isRoot = false
    private val prefs: PreferenceBuilder by lazy { PreferenceBuilder(baseContext, PreferenceBuilder.DefaultFilename) }
    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        AdsUtil.initAds(this, R.id.adView)

        val fab = findViewById<FloatingActionButton>(R.id.fab)

        isRoot = prefs.getBoolean("root", false)

        val WarningPermissionError = findViewById<CardView>(R.id.ErrorPermissionCard)

        Thread {
            WarningPermissionError.setOnClickListener { view ->
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

        Assent.setActivity(this@CompleteUnrootActivity, this@CompleteUnrootActivity)
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            WarningPermissionError.visibility = View.VISIBLE
            Assent.requestPermissions({ result: PermissionResultSet ->
                if (result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    WarningPermissionError.visibility = View.GONE
                    fab!!.show()
                } else if (!result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    WarningPermissionError.visibility = View.VISIBLE
                    fab!!.hide()
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE)
        } else {
            WarningPermissionError.visibility = View.GONE
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        assert(fab != null)
        fab!!.setOnClickListener { view ->
            if (isRoot) {
                executor.execAsRoot(Core.misc.init)

                MaterialDialog.Builder(this@CompleteUnrootActivity)
                        .title(R.string.DialogFullTitle)
                        .content(R.string.DialogFullContent)
                        .positiveText(R.string.DialogContinue)
                        .negativeText(R.string.DialogCancel)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.DeepOrange_500)
                        .onPositive { dialog, which ->
                            val unrootDialog = MaterialDialog.Builder(this@CompleteUnrootActivity)
                                    .title(R.string.UnrootDialogTitle)
                                    .content(R.string.UnrootDialogInit)
                                    .progress(true, 0)
                                    .progressIndeterminateStyle(false)
                                    .backgroundColorRes(R.color.Grey_800)
                                    .show()


                            Handler().postDelayed({ unrootDialog.setContent("unroot and wipe in progress, It can take minutes.\nDon't touch for any reason your device!") }, 1000)
                            
                            executor.exec(Core.misc.mountRW)
                            executor.exec(Core.unroot.battery_stats)
                            if (!BuildConfig.DEBUG) executor.exec(Core.unroot.disable_wireless_debug)
                            executor.exec(Core.unroot.kill_all)
                            executor.exec(Core.unroot.remove_extra_bins)
                            executor.exec(Core.unroot.remove_init)
                            executor.exec(Core.unroot.remove_busybox)
                            executor.exec(Core.unroot.clean_sdcard)
                            executor.exec(Core.unroot.uninstall_pm)

                            executor.exec(Core.unroot.unroot)

                            executor.exec(Core.unroot.remove_magisk)
                            executor.exec(Core.unroot.remove_su)
                            executor.exec(Core.unroot.erase_data_root)
                            executor.autoReboot()
                            executor.matchPackages()
                            //executor.execQueue();


                        }
                        .onNegative { ConfirmDialog, which -> ConfirmDialog.dismiss() }
                        .show()
            } else {
                MaterialDialog.Builder(this@CompleteUnrootActivity)
                        .title(R.string.CheckDialogTitle)
                        .content(R.string.CheckDialogContent)
                        .positiveText(R.string.CheckDialogConfirm)
                        .negativeText(R.string.CheckDialogExit)
                        .autoDismiss(true)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.Red_500)
                        .onPositive { dialog, which -> dialog.dismiss() }
                        .onNegative { dialog, which -> onBackPressed() }
                        .show()
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this@CompleteUnrootActivity, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)!!
        navigationView.setNavigationItemSelectedListener(this)

        val i1 = findViewById<ImageView>(R.id.check_image)
        val i2 = findViewById<ImageView>(R.id.hw_check_image)
        val c1 = findViewById<TextView>(R.id.check_text)
        val c2 = findViewById<TextView>(R.id.hw_check_text)
        AssetsProvider.init(isRoot, i1, i2, c1, c2)

    }

    override fun onResume() {
        super.onResume()
        Assent.setActivity(this@CompleteUnrootActivity, this@CompleteUnrootActivity)
        val WarningPermissionError = findViewById<CardView>(R.id.ErrorPermissionCard)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            WarningPermissionError.visibility = View.VISIBLE
            fab.hide()
        } else {
            WarningPermissionError.visibility = View.GONE
            fab.show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing)
            Assent.setActivity(this@CompleteUnrootActivity, this@CompleteUnrootActivity)
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
            val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)!!
            drawer.openDrawer(GravityCompat.START)
            return true
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.nav_erase) {
            val erase = Intent(this@CompleteUnrootActivity, WipeActivity::class.java)
            startActivity(erase)
        } else if (id == R.id.nav_unroot) {
            val unroot = Intent(this@CompleteUnrootActivity, UnrootActivity::class.java)
            startActivity(unroot)
        } else if (id == R.id.nav_reboot) {
            val reboot = Intent(this@CompleteUnrootActivity, RebootActivity::class.java)
            startActivity(reboot)
        } else if (id == R.id.nav_info) {
            val info = Intent(this@CompleteUnrootActivity, AboutActivity::class.java)
            startActivity(info)
        }


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)!!
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}