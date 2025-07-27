package jatx.russianrocksongbook.commonviewmodel.deps.impl

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import it.czerwinski.android.hilt.annotations.BoundTo
import jatx.russianrocksongbook.commonviewmodel.deps.Resources
import javax.inject.Inject

@ViewModelScoped
@BoundTo(supertype = Resources::class, component = ViewModelComponent::class)
class ResourcesImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
): Resources {
    override fun getString(@StringRes resId: Int, vararg args: Any) = context
        .getString(resId, *args)
}