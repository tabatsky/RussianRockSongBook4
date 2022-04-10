package jatx.russianrocksongbook.viewmodel.deps

import androidx.annotation.StringRes

interface Toasts {
    fun showToast(toastText: String)

    fun showToast(@StringRes resId: Int)
}