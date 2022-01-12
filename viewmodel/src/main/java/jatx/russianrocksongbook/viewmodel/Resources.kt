package jatx.russianrocksongbook.viewmodel

import android.content.Context
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Resources @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes resId: Int, vararg args: Any) = context
        .getString(resId, *args)
}