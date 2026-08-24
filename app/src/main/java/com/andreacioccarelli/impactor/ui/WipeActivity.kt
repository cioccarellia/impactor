package com.andreacioccarelli.impactor.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.MaterialDialog
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.ImpactorActivity
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WipeActivity : ImpactorActivity() {

    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.erase)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)


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


}