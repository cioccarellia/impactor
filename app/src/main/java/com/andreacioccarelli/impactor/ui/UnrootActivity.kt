package com.andreacioccarelli.impactor.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.afollestad.assent.Assent
import com.afollestad.assent.AssentCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.BuildConfig
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.BaseActivity
import com.andreacioccarelli.impactor.tools.AssetsProvider
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import com.andreacioccarelli.impactor.tools.PreferenceBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class UnrootActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var canReadExternalStorage = true

    private lateinit var mBuilder: PreferenceBuilder
    private lateinit var executor: CodeExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unroot)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        mBuilder = PreferenceBuilder(baseContext, PreferenceBuilder.DefaultFilename)
        executor = CodeExecutor()

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this@UnrootActivity, drawer, toolbar, R.string.DrawerOpen, R.string.DrawerClose)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        this.title = resources.getString(R.string.TitleUnroot)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val cardPermission = findViewById<androidx.cardview.widget.CardView>(R.id.ErrorPermissionCard)

        CoroutineScope(Dispatchers.Main).launch {
            var startCounter = mBuilder.getInt("startCounter", 0)

            cardPermission.setOnClickListener {

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

            if (canReadExternalStorage) {
                mBuilder.putInt("startCounter", ++startCounter)
            }
        }

        Assent.setActivity(this@UnrootActivity, this@UnrootActivity)
        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            cardPermission.visibility = View.VISIBLE

            Assent.requestPermissions(AssentCallback {
                if (it.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    cardPermission.visibility = View.GONE
                    fab.show()
                } else if (it.isGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
                    cardPermission.visibility = View.VISIBLE
                    fab.hide()
                }
            }, 69, Assent.WRITE_EXTERNAL_STORAGE)
        } else {
            cardPermission.visibility = View.GONE
        }

        fab.setOnClickListener { view ->
            if (mBuilder.getBoolean("root", false)) {
                MaterialDialog.Builder(this@UnrootActivity)
                        .title(R.string.DialogUnrootTitle)
                        .content(R.string.DialogUnrootContent)
                        .positiveText(R.string.DialogContinue)
                        .negativeText(R.string.DialogCancel)
                        .autoDismiss(true)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.DeepOrange_500)
                        .onPositive { dialog, which ->
                            val unrootDialog = MaterialDialog.Builder(this@UnrootActivity)
                                    .title(R.string.UnrootDialogTitle)
                                    .content(R.string.UnrootDialogInit)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .progressIndeterminateStyle(false)
                                    .backgroundColorRes(R.color.Grey_800)
                                    .show()


                            Handler().postDelayed({ unrootDialog.setContent("Unroot in progress, It can take up to minutes.\nDon't touch for any reason your device!") }, 1000)

                            CoroutineScope(Dispatchers.IO).launch {
                                executor.exec(Core.misc.mountRW)
                                executor.exec(Core.unroot.battery_stats)

                                if (!BuildConfig.DEBUG)
                                    executor.exec(Core.unroot.disable_wireless_debug)

                                executor.exec(Core.unroot.remove_extra_bins)
                                executor.exec(Core.unroot.remove_init)
                                executor.exec(Core.unroot.remove_busybox)
                                executor.exec(Core.unroot.clean_sdcard)
                                executor.exec(Core.unroot.uninstall_pm)

                                executor.matchPackages()
                                executor.exec(Core.unroot.unroot)

                                executor.exec(Core.unroot.remove_magisk)
                                executor.exec(Core.unroot.remove_su)
                                executor.autoReboot()
                            }

                        }
                        .onNegative { dialog, which -> dialog.dismiss() }
                        .show()
            } else {
                MaterialDialog.Builder(this@UnrootActivity)
                        .title(R.string.CheckDialogTitle)
                        .content(R.string.CheckDialogContent)
                        .positiveText(R.string.CheckDialogConfirm)
                        .negativeText(R.string.CheckDialogExit)
                        .backgroundColorRes(R.color.Grey_800)
                        .positiveColorRes(R.color.Red_500)
                        .onPositive { dialog, which -> dialog.dismiss() }
                        .onNegative { dialog, which ->
                            dialog.dismiss()
                            onBackPressed()
                        }
                        .show()
            }
        }


        val i1 = findViewById<ImageView>(R.id.check_image)
        val i2 = findViewById<ImageView>(R.id.hw_check_image)
        val c1 = findViewById<TextView>(R.id.check_text)
        val c2 = findViewById<TextView>(R.id.hw_check_text)

        AssetsProvider.init(baseContext, i1, i2, c1, c2)
    }

    override fun onResume() {
        super.onResume()
        Assent.setActivity(this@UnrootActivity, this@UnrootActivity)

        val permissionCard = findViewById<androidx.cardview.widget.CardView>(R.id.ErrorPermissionCard)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        if (!Assent.isPermissionGranted(Assent.WRITE_EXTERNAL_STORAGE)) {
            permissionCard.visibility = View.VISIBLE
            canReadExternalStorage = false
            fab.hide()
        } else {
            permissionCard.visibility = View.GONE
            fab.show()
        }
    }


    override fun onPause() {
        super.onPause()
        if (isFinishing) Assent.setActivity(this@UnrootActivity, this@UnrootActivity)
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

        when (id) {
            R.id.nav_impactor -> {
                val impactor = Intent(this@UnrootActivity, CompleteUnrootActivity::class.java)
                startActivity(impactor)
            }
            R.id.nav_erase -> {
                val erase = Intent(this@UnrootActivity, WipeActivity::class.java)
                startActivity(erase)
            }
            R.id.nav_reboot -> {
                val reboot = Intent(this@UnrootActivity, RebootActivity::class.java)
                startActivity(reboot)
            }
            R.id.nav_info -> {
                val info = Intent(this@UnrootActivity, AboutActivity::class.java)
                startActivity(info)
            }
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
