package jatx.russianrocksongbook.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Toasts @Inject constructor(
    @ApplicationContext private val context: Context,
    private val resources: Resources
) {
    fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        val toastText = resources.getString(resId)
        showToast(toastText)
    }
}