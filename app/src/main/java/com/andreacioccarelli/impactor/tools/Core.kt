package com.andreacioccarelli.impactor.tools

class Core {

    /*
    *
    *  @N = External Storage
    *
    * */

    object misc {
        val mountRW = arrayOf("mount -o remount,rw,remount,rw,remount /system", "mount -o remount,rw,remount,rw,remount /data", "mount -o remount,rw,remount,rw,remount @N", "mount -o remount,rw,remount,rw,remount \"/*subackup\"", "mount -o remount,rw,remount,rw,remount /", "mount -o remount,rw,remount,rw,remount /magisk", "mount -o remount,rw,remount,rw,remount /sbin")
        val init = arrayOf("su")
    }

    object unroot {
        val battery_stats = arrayOf("mount -o remount,rw,remount /data", "rm -rf /data/system/batterystats.bin")
        val disable_wireless_debug = arrayOf("setprop service.adb.tcp.port -1", "stop adbd && killall adbd")
        val remove_extra_bins = arrayOf("cd /system/bin", "rm -rf .ku", "rm -rf .usr", "rm -rf .ext", "rm -rf .krsh", "rm -rf sudo", "rm -rf busybox", "cd /system/xbin", "rm -rf .ku", "rm -rf .usr", "rm -rf .ext", "rm -rf .krsh", "rm -rf sudo", "rm -rf busybox", "rm -rf /data/data-lib/com.kingroot.kinguser", "rm -rf /data/data-lib/kds", "rm -rf /data/data-lib/tps", "rm -rf /data/user/0/*kingroot*", "rm -rf /data/user/0/com.kingroot.kinguser", "rm -rf /data/data-lib/com.kingstudio.purify")
        val remove_busybox = arrayOf("rm -rf $(which busybox)", "rm -rf /system/bin/busybox", "rm -rf /system/xbin/busybox", "rm -rf /system/sbin/busybox", "rm -rf /vendor/bin/busybox", "rm -rf /sbin/busybox", "rm -rf /sbin/busybox")
        val remove_su = arrayOf("rm -rf $(which su)", "rm -rf /system/bin/su", "rm -rf /system/xbin/su", "rm -rf /su", "rm -rf /magisk/.core/su", "rm -rf /magisk/.core/su/sbin_bind/su", "rm -rf /system/sbin/su", "rm -rf /sbin/su", "rm -rf /system/su.d")
        val uninstall_pm = arrayOf("rm -rf /data/app/*eu.chainfire.supersu*", "rm -rf /data/data/*eu.chainfire.supersu*", "rm -rf /system/app/*supersu*", "rm -rf /data/app-lib/*Super*", "rm -rf /system/app/*Super*", "rm -rf /data/data/*com.kingroot.kinguser*", "rm -rf /data/app/*com.kingroot.kinguser*", "rm -rf /system/app/*kingroot*", "rm -rf /system/app/*King*", "rm -rf /data/data/*com.kingoapp.root*", "rm -rf /data/app/*com.kingoapp.root*", "rm -rf /system/app/*kingo*", "rm -rf /data/data/*com.yellowes.su*", "rm -rf /data/app/*com.yellowes.su*", "rm -rf /system/app/*yellowes*", "rm -rf /data/data/*com.noshufou.android.su*", "rm -rf /data/app/*com.noshufou.android.su*", "rm -rf /system/app/*noshufou*", "rm -rf /data/app/*com.mgyun.shua.su*", "rm -rf /data/data/*com.mgyun.shua.su*", "rm -rf /system/app/*com.mgyun.shua.su*", "rm -rf /data/data/*org.masteraxe.superuser*", "rm -rf /data/app/*org.masteraxe.superuser*", "rm -rf /system/app/*superuser*", "rm -rf /data/data/*com.kingroot.purify*", "rm -rf /data/app/*com.kingroot.purify*", "rm -rf /data/app/*com.kingroot.rushroot*", "rm -rf /data/data/*com.kingroot.rushroot*", "rm -rf /system/app/rushroot", "rm -rf /system/app/*magisk*", "rm -rf /data/app/*com.topjohnwu.magisk*", "rm -rf /data/data/*com.topjohnwu.magisk*", "rm -rf /data/app/*me.phh.superuser*", "rm -rf /data/data/*me.phh.superuser*", "rm -rf /data/app/*com.m0narx.su*", "rm -rf /data/data/*com.m0narx.su*", "rm -rf /data/app/*com.koushikdutta.superuser*", "rm -rf /data/data/*com.koushikdutta.superuser*", "rm -rf /system/app/*iroot*", "rm -rf /system/app/*King*", "rm -rf /system/app/*Magisk*", "pm uninstall com.kingstudio.purify", "rm -rf /data/data/com.kingstudio.purify", "rm -rf /data/app/com.kingstudio.purify*")

        val erase_data_root = arrayOf("rm -rf /data", "mount -o remount,rw,remount,rw,remount /data", "rm -rf /data", "rmdir /data", "wipe data", "reboot")

        val kill_all = arrayOf("am force-stop eu.chainfire.supersu", "am force-stop com.kingroot.kinguser", "am force-stop com.topjohnwu.magisk", "am force-stop com.m0narx.su", "am force-stop me.phh.superuser", "am force-stop com.noshufou.android.su", "am force-stop com.koushikdutta.superuser", "am force-stop com.yellowes.su", "am force-stop com.kingoapp.root", "am force-stop com.kingroot.rushroot")

        val unroot = arrayOf("rm -rf /system/xbin/sugote-mksh", "rm -rf /system/bin/ddexe_real", "rm -rf /system/xbin/ku.sud", "rm -rf /system/usr/ikm/ikmsu", "rm -rf /system/usr/ikm/ikmsu", "rm -rf /system/usr/ikm/ikmsu", "rm -rf /.subackup", "rm -rf \"/.subackup\"", "rm -rf /system/bin/.ext/", "rm -rf /data/su.img", "rm -rf /magisk", "cd /", "rm -rf *supersu* *superuser* impactor *daemonsu* *sukernel* *magisk.rc*", "rm -rf /system/usr/we-need-root/su-backup", "rm -rf /system/usr/we-need-root", "rm -rf /system/su.d", "rm -rf /.subackup", "rm -rf $(which su)", "rm -rf $(which sudo)", "rm -rf $(which busybox)")

        val clean_sdcard = arrayOf("rm -rf @N/*stock-conf*", "rm -rf @N/*kr*", "rm -rf @N/qqsecure", "rm -rf @N/.*", "rm -rf @N/com.kingroot.kinguser", "rm -rf @N/com.kingstudio.purify", "rm -rf @N/Kingroot", "rm -rf @N/Kinguserdown", "rm -rf @N/Tencent", "rm -rf @N/*kingroot*")

        val remove_magisk = arrayOf("rm -rf /data/*magisk*", "rm -rf /data/magisk.img", "rm -rf /magisk/.core/su/su", "rm -rf /magisk/.core/su/sbin_bind/su", "rm -rf /magisk/.core/post-fs-data.d", "rm -rf /magisk/.core/su", "rm -rf /data/data/*com.topjohnwu.magisk*", "rm -rf /data/app/*com.topjohnwu.magisk*", "rm -rf /system/app/*com.topjohnwu.magisk*", "rm -rf /system/app/*magisk*", "rm -rf /cache/magisk.log /cache/last_magisk.log /cache/magiskhide.log /cache/.disable_magisk", "rm -rf /cache/magisk", "rm -rf /cache/magisk_merge /cache/magisk_mount", "rm -rf /cache/unblock /cache/magisk_uninstaller.sh", "rm -rf /data/magisk", "rm -rf /data/magisk.img", "rm -rf /data/magisk_merge.img", "rm -rf /data/busybox", "rm -rf /data/magisk", "rm -rf /data/custom_ramdisk_patch.sh 2>/dev/null", "rm -rf /sbin/magisk", "rm -rf /sbin/magiskhide", "rm -rf /sbin/su", "rm -rf rm -rf /sbin/magiskpolicy", "rm -rf /sbin/supolicy", "rm -rf /sbin/magiskinit", "rm -rf /sbin/resetprop", "rm -rf $(which supolicy)", "rm -rf $(which magisk)")

        val remove_init = arrayOf("mount -o remount,rw,remount,rw,remount /", "rm -rf /init.superuser.rc", "rm -rf /init.supersu.rc", "rm -rf /system/etc/init.d/99SuperSUDaemon", "rm -rf /system/etc/.installed_su_daemon", "rm -rf /sbin/launch_daemonsu.sh", "rm -rf /init.d_support.sh", "rm -rf /init.magisk.rc")

    }

    object reboot {
        val Reboot = arrayOf("reboot")
        val Shutdown = arrayOf("reboot -p")
        val RebootRecovery = arrayOf("reboot recovery")
        val RestartUI = arrayOf("killall com.android.systemui")
        val RebootBootloader = arrayOf("reboot bootloader")
        val RebootSafemode = arrayOf("setprop persist.sys.safemode 1", "reboot")
        val FastReboot = arrayOf("setprop ctl.restart zygote", "killall system_server")
    }
}
