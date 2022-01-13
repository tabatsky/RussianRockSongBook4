package jatx.russianrocksongbook.viewmodel.sideeffects

import androidx.annotation.StringRes

interface Toasts {
    fun showToast(toastText: String)

    fun showToast(@StringRes resId: Int)
}