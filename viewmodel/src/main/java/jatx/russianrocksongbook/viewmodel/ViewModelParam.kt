package jatx.russianrocksongbook.viewmodel

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import jatx.russianrocksongbook.database.api.SongRepository
import jatx.russianrocksongbook.filesystem.data.api.FileSystemAdapter
import jatx.russianrocksongbook.preferences.api.Settings
import jatx.russianrocksongbook.networking.api.SongBookAPIAdapter
import jatx.russianrocksongbook.preferences.api.UserInfo
import jatx.russianrocksongbook.preferences.api.Version
import javax.inject.Inject

@ActivityRetainedScoped
class ViewModelParam @Inject constructor(
    val songBookAPIAdapter: SongBookAPIAdapter,
    val settings: Settings,
    val callbacks: Callbacks,
    @ApplicationContext val context: Context,
    val songRepo: SongRepository,
    val userInfo: UserInfo,
    val version: Version,
    val fileSystemAdapter: FileSystemAdapter
)