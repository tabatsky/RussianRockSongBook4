package jatx.russianrocksongbook.commonviewmodel.deps.impl

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.commonviewmodel.deps.Resources
import jatx.russianrocksongbook.commonviewmodel.deps.Toasts
import javax.inject.Inject

@ViewModelScoped
@BoundTo(supertype = Toasts::class, component = ViewModelComponent::class)
internal class ToastsImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: Resources
): Toasts {
    override fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    override fun showToast(@StringRes resId: Int) {
        val toastText = resources.getString(resId)
        showToast(toastText)
    }
}