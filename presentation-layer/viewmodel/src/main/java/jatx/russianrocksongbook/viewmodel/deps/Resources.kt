package jatx.russianrocksongbook.viewmodel.deps

import androidx.annotation.StringRes

interface Resources {
    fun getString(@StringRes resId: Int, vararg args: Any): String
}