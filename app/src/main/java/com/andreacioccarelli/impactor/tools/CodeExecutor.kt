package com.andreacioccarelli.impactor.tools

import android.os.Environment
import android.util.Log

import com.andreacioccarelli.impactor.BuildConfig
import com.jrummyapps.android.shell.CommandResult
import com.jrummyapps.android.shell.Shell
import com.jrummyapps.android.shell.ShellExitCode

class CodeExecutor : ShellExitCode {

    private var CommandQueue = ""
    private var c = 0
    private var execAutoreboot: Boolean = false

    init {
        CommandQueue = ""
        c = 0
        execAutoreboot = false
    }

    fun checkBusyBox(): Boolean {
        val check = Shell.SH.run("busybox")
        return check.exitCode != ShellExitCode.COMMAND_NOT_FOUND
    }

    private fun execAsRoot(ShellCode: String): CommandResult {
        return Shell.SU.run(ShellCode.replace("@N", Environment.getExternalStorageDirectory().absolutePath))
    }

    fun execAsRoot(ShellCode: Array<String>): CommandResult {
        val builder = StringBuilder()
        for (s in ShellCode) {
            builder.append(s).append("\n")
        }
        val cleanCommand = builder.toString()
        return Shell.SU.run(cleanCommand.replace("@N", Environment.getExternalStorageDirectory().absolutePath))
    }

    fun exec(cmd: Array<String>) {
        val builder = StringBuilder()
        for (s in cmd) {
            builder.append(s).append("\n")
        }
        val cleanCommand = builder.toString().replace("@N", Environment.getExternalStorageDirectory().absolutePath)
        val e = execAsRoot(cleanCommand)

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "-----------------------------------------------------------------------------")
            Log.d(TAG, "[[Log for command number $c]]")

            Log.d(TAG, "Input:\n$cleanCommand")
            Log.d(TAG, "Stdout:\n${e.getStdout()}")
            Log.d(TAG, "Stderr:\n${e.getStderr()}")
            Log.d(TAG, "Exit Code: ${e.exitCode}")
            Log.d(TAG, "Successful: ${e.isSuccessful}")
        }
        c++
    }

    fun execQueue(): CommandResult {
        val e = Shell.SU.run(CommandQueue)
        Log.w(TAG, CommandQueue)
        Log.w(TAG, "\nStdout\"")
        Log.w(TAG, e.getStdout())
        Log.w(TAG, "\nStderr\"")
        Log.w(TAG, e.getStderr())
        Log.w(TAG, "\nExit\"")
        Log.w(TAG, e.exitCode.toString())
        return e
    }

    fun matchPackages() {
        if (c < 9) Log.e("ImpactorCore", "C = " + c.toString() + ". Suspicious condition")
        Log.e("ImpactorCore", "Shell ids: " + Shell.SU.run("id").getStdout())
        Thread {
            var path: String
            var installed: Boolean
            val targets = arrayOf("eu.chainfire.supersu", "com.kingroot.kinguser", "com.kingroot.purify", "com.kingstudio.purify", "com.kingoapp.root", "com.yellowes.su", "com.noshufou.android.su", "com.mgyun.shua.su", "org.masteraxe.superuser", "com.topjohnwu.magisk", "me.phh.superuser", "com.m0narx.su")
            for (i in targets) {
                path = ""
                try {
                    path = Shell.SU.run("pm path $i").getStdout().split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
                    Log.w("ImpactorCore", "Path: $path")
                    installed = true
                } catch (e: ArrayIndexOutOfBoundsException) {
                    installed = false
                }

                Log.w("ImpactorCore", i + ": " + if (installed) "Installed" else "Not Installed")
                if (installed) {
                    Shell.SU.run("rm -rf $path")
                    if (path.contains("/system/app")) {
                        Log.w("ImpactorCore", "Found in /system/app")
                        try {
                            val path2 = path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1] + path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[2] + path.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[3]
                            val op = Shell.SU.run("rm -rf $path2").getStdout()
                            Log.w("ImpactorCore", op)

                        } catch (e: ArrayIndexOutOfBoundsException) {
                            // old APIs
                            Log.w("ImpactorCore", "Old APIs")
                        }

                    }
                    if (path.contains("/data/")) {
                        Log.w("ImpactorCore", "Found in /data/")
                        Shell.SU.run("rm -rf /data/data/*$i*")
                        Shell.SU.run("rm -rf /data/app/*$i*")
                        Shell.SU.run("rm -rf /data/app-lib/*$i*")
                        Shell.SU.run("sed -i \"/$i/d\" /data/system/packages.list")
                    }
                }
            }
            if (execAutoreboot) Shell.SU.run("reboot")
            Log.d("ImpactorCore", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            Log.d("ImpactorCore", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        }.start()

    }

    fun autoReboot() {
        this.execAutoreboot = true
    }

    companion object {

        internal val TAG = "ImpactorCore"
    }
}

