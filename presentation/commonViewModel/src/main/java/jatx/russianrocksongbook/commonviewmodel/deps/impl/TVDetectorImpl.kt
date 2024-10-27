package jatx.russianrocksongbook.commonviewmodel.deps.impl

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.commonviewmodel.deps.TVDetector
import javax.inject.Inject

@ViewModelScoped
@BoundTo(supertype = TVDetector::class, component = ViewModelComponent::class)
class TVDetectorImpl @Inject constructor(
    @ApplicationContext private val context: Context
): TVDetector {
    override val isTV: Boolean
        get() {
            return try {
                val uiModeManager =
                    context.getSystemService(Context.UI_MODE_SERVICE) as? UiModeManager
                uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
            } catch (e: Throwable) {
                Log.e("tvDetector", "error", e)
                false
            }
        }
}