package jatx.russianrocksongbook.commonviewmodel.deps.impl

import androidx.annotation.StringRes
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.TestBoundTo
import jatx.russianrocksongbook.commonviewmodel.deps.Resources
import jatx.russianrocksongbook.commonviewmodel.deps.Toasts
import java.util.concurrent.ArrayBlockingQueue
import javax.inject.Inject

@ViewModelScoped
@TestBoundTo(supertype = Toasts::class, component = ViewModelComponent::class)
class ToastsTestImpl @Inject constructor(
    private val resources: Resources
): Toasts {
    override fun showToast(toastText: String) {
        textQueue.put(toastText)
    }

    override fun showToast(@StringRes resId: Int) {
        val toastText = resources.getString(resId)
        textQueue.put(toastText)
    }

    companion object {
        val textQueue = ArrayBlockingQueue<String>(100)

        fun verifyText(toastText: String) = try {
            textQueue.poll()!! == toastText
        } catch (_: Exception) {
            false
        }
    }
}