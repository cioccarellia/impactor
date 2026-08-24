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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class UnrootActivity : ImpactorActivity() {

    private lateinit var executor: CodeExecutor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.unroot)

        executor = CodeExecutor()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)


        this.title = resources.getString(R.string.TitleUnroot)
        val fab = findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener { view ->
            if (root) {
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


                            CoroutineScope(Dispatchers.Main).launch {
                                delay(500)
                                unrootDialog.setContent(getString(R.string.unroot_progress))
                            }

                            CoroutineScope(Dispatchers.IO).launch {
                                executor.exec(Core.misc.mountRW)
                                executor.exec(Core.unroot.battery_stats)
                                if (!BuildConfig.DEBUG) executor.exec(Core.unroot.disable_wireless_debug)
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
                            finish()
                        }
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
