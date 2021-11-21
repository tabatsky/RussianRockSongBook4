package jatx.russianrocksongbook.model.preferences

import android.content.Context
import android.content.pm.PackageManager

object Version {
    var appVersionName = "undefined"
    var appVersionCode = 0

    fun init(context: Context) {
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            appVersionName = pInfo.versionName
            appVersionCode = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
        }
    }
}