package com.andreacioccarelli.impactor.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.BuildConfig
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.ImpactorActivity
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CompleteUnrootActivity : ImpactorActivity() {

    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab?.setOnClickListener { view ->
            if (root) {
                // Warms up the superuser prompt. Blocking, so it cannot run on the main thread.
                lifecycleScope.launch(Dispatchers.IO) { executor.execAsRoot(Core.misc.init) }

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


                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                unrootDialog.setContent(getString(R.string.unroot_wipe_progress))
                            }

                            CoroutineScope(Dispatchers.IO).launch {
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
                        .onNegative { dialog, which -> finish() }
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


}