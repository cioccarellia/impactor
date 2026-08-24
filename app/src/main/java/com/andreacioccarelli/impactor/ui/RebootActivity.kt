package com.andreacioccarelli.impactor.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.andreacioccarelli.impactor.R
import com.andreacioccarelli.impactor.base.ImpactorActivity
import com.andreacioccarelli.impactor.tools.CodeExecutor
import com.andreacioccarelli.impactor.tools.Core
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RebootActivity : ImpactorActivity() {

    private val executor: CodeExecutor by lazy { CodeExecutor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reboot)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setupDrawer(toolbar)

        bind(R.id.card_reboot, Core.reboot.Reboot, "Error while rebooting device")
        bind(R.id.card_shutdown, Core.reboot.Shutdown, "Error while powering off device")
        bind(R.id.card_recovery, Core.reboot.RebootRecovery, "Error while rebooting device")
        bind(R.id.card_ui, Core.reboot.RestartUI, "Error while restarting UI")
        bind(R.id.card_bootloader, Core.reboot.RebootBootloader, "Error while rebooting device")
        bind(R.id.card_safe, Core.reboot.RebootSafemode, "Error while rebooting device")
        bind(R.id.card_soft_reboot, Core.reboot.FastReboot, "Error while rebooting device")
    }

    /**
     * Requesting root and running the command blocks for as long as the superuser prompt is on
     * screen, so it has to stay off the main thread.
     */
    private fun bind(cardId: Int, commands: Array<String>, errorMessage: String) {
        findViewById<CardView>(cardId).setOnClickListener {
            lifecycleScope.launch {
                val exitCode = withContext(Dispatchers.IO) { executor.execAsRoot(commands).exitCode }

                if (exitCode != 0) {
                    Toasty.error(this@RebootActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
