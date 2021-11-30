package jatx.russianrocksongbook.viewmodel

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.model.data.FileSystemAdapter
import jatx.russianrocksongbook.model.data.SongBookAPIAdapter
import jatx.russianrocksongbook.model.data.SongRepository
import jatx.russianrocksongbook.model.preferences.Settings
import jatx.russianrocksongbook.model.preferences.UserInfo
import javax.inject.Inject

@ActivityRetainedScoped
class ViewModelParam @Inject constructor(
    val songBookAPIAdapter: SongBookAPIAdapter,
    val settings: Settings,
    val callbacks: Callbacks,
    @ApplicationContext val context: Context,
    val songRepo: SongRepository,
    val userInfo: UserInfo,
    val fileSystemAdapter: FileSystemAdapter
)