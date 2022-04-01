package jatx.russianrocksongbook.preferences.models

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.domain.models.appcrash.Version
import javax.inject.Inject
import javax.inject.Singleton

@Suppress("DEPRECATION")
@Singleton
@BoundTo(supertype = Version::class, component = SingletonComponent::class)
class VersionImpl @Inject constructor(
    @ApplicationContext context: Context
): Version {
    override var appVersionName = "undefined"
    override var appVersionCode = 0

    init {
        try {
            val pInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            appVersionName = pInfo.versionName
            appVersionCode = pInfo.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
        }
    }
}