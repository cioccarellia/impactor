package com.andreacioccarelli.impactor.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.CardView
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.afollestad.assent.Assent
import com.afollestad.assent.PermissionResultSet
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.tools.Core
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.BaseActivity
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.AssetsProvider
import com.andreacioccarelli.impactor.tools.PreferenceBuilder
import org.jetbrains.anko.doAsync

class WipeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isRoot = false
    private val prefs: PreferenceBuilder by lazy { PreferenceBuilder(baseContext, PreferenceBuilder.DefaultFilename) }
    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.erase)

        val fab = findViewById<FloatingActionButton>(R.id.fab)

        isRoot = prefs.getBoolean("root", false)

        val WarningPermissionError = findViewById<CardView>(R.id.ErrorPermissionCard)

        val t = Thread {
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
        }
        t.start()

        Assent.setActivity(this@WipeActivity, this@WipeActivity)
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            WarningPermissionError.visibility = View.VISIBLE
            Assent.requestPermissions({ result: PermissionResultSet ->
                if (result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    WarningPermissionError.visibility = View.GONE
                    fab.show()
                } else if (!result.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    WarningPermissionError.visibility = View.VISIBLE
                    fab.hide()
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE)
        } else {
            WarningPermissionError.visibility = View.GONE
        }


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            if (isRoot) {
                MaterialDialog.Builder(this@WipeActivity)
                        .title(R.string.DialogEraseTitle)
                        .content(R.string.DialogEraseContent)
                        .positiveText(R.string.DialogContinue)
                        .negativeText(R.string.DialogCancel)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.DeepOrange_500)
                        .onPositive { _, _ ->
                            if (isRoot) {
                                val unrootDialog = MaterialDialog.Builder(this@WipeActivity)
                                        .title(R.string.ProgressDialogWipeTitle)
                                        .content("Initializing environment..")
                                        .cancelable(false)
                                        .progress(true, 0)
                                        .progressIndeterminateStyle(false)
                                        .backgroundColorRes(R.color.Grey_800)
                                        .show()

                                Handler().postDelayed({ unrootDialog.setContent("Wiping /data and /cache...") }, 300)


                                doAsync {
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
                        .title("Continue?")
                        .content("On this device isn't installed root access,you will be redirected to the System Settings app from where you can wipe your device without root")
                        .positiveText("OPEN SETTINGS")
                        .negativeText("CANCEL")
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.Red_500)
                        .autoDismiss(true)
                        .onPositive { dialog, which -> startActivity(Intent(Settings.ACTION_SETTINGS)) }
                        .onNegative { dialog, which -> dialog.dismiss() }
                        .show()
            }
        }


        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this@WipeActivity, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        assert(drawer != null)
        drawer!!.setDrawerListener(toggle)
        toggle.syncState()

        val i1 = findViewById<ImageView>(R.id.check_image)
        val i2 = findViewById<ImageView>(R.id.hw_check_image)
        val c1 = findViewById<TextView>(R.id.check_text)
        val c2 = findViewById<TextView>(R.id.hw_check_text)
        AssetsProvider.init(isRoot, i1, i2, c1, c2)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)!!
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()
        Assent.setActivity(this@WipeActivity, this@WipeActivity)
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
            Assent.setActivity(this@WipeActivity, this@WipeActivity)
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