package jatx.russianrocksongbook.preferences.internal.impl

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.preferences.api.Version
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = Version::class, component = SingletonComponent::class)
internal class VersionImpl @Inject constructor(
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