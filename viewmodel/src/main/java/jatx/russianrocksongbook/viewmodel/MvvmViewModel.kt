package jatx.russianrocksongbook.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.db.util.applySongPatches
import jatx.russianrocksongbook.model.db.util.deleteWrongArtists
import jatx.russianrocksongbook.model.db.util.deleteWrongSongs
import jatx.russianrocksongbook.model.db.util.fillDbFromJSON
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.interfaces.Cloud
import jatx.russianrocksongbook.viewmodel.interfaces.Local
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
open class MvvmViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val screenStateHolder: ScreenStateHolder
): ViewModel() {
    val songBookAPIAdapter = viewModelParam.songBookAPIAdapter
    val settings = viewModelParam.settings
    val actions = viewModelParam.actions
    val context = viewModelParam.context
    val songRepo = viewModelParam.songRepo
    val userInfo = viewModelParam.userInfo
    val fileSystemAdapter = viewModelParam.fileSystemAdapter

    val currentScreenVariant = screenStateHolder
        .currentScreenVariant
        .asStateFlow()

    val currentArtist = screenStateHolder
        .currentArtist
        .asStateFlow()

    val artistList = screenStateHolder
        .artistList
        .asStateFlow()

    val appWasUpdated = screenStateHolder
        .appWasUpdated
        .asStateFlow()

    private val _showUploadDialogForDir = MutableStateFlow(false)
    val showUploadDialogForDir = _showUploadDialogForDir.asStateFlow()
    private val _showUploadDialogForSong = MutableStateFlow(false)
    val showUploadDialogForSong = _showUploadDialogForSong.asStateFlow()
    private val _uploadArtist = MutableStateFlow("")
    val uploadArtist = _uploadArtist.asStateFlow()
    private val _uploadSongList = MutableStateFlow<List<Song>>(listOf())
    val uploadSongList = _uploadSongList.asStateFlow()
    private val _newSong: MutableStateFlow<Song?> = MutableStateFlow(null)
    val newSong = _newSong.asStateFlow()

    private var getArtistsDisposable: Disposable? = null
    private var uploadListDisposable: Disposable? = null
    private var uploadSongDisposable: Disposable? = null

    fun back(onFinish: () -> Unit = {}) {
        Log.e("current screen", currentScreenVariant.value.toString())
        when (currentScreenVariant.value) {
            CurrentScreenVariant.START, CurrentScreenVariant.SONG_LIST -> {
                onFinish()
            }
            CurrentScreenVariant.CLOUD_SONG_TEXT -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH, true)
            }
            CurrentScreenVariant.SONG_TEXT -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST)
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE)
                }
            }
            else -> {
                if (currentArtist.value != ARTIST_FAVORITE) {
                    selectScreen(CurrentScreenVariant.SONG_LIST, false)
                } else {
                    selectScreen(CurrentScreenVariant.FAVORITE, false)
                }
            }
        }
    }

    fun selectScreen(
        screen: CurrentScreenVariant,
        isBackFromSong: Boolean = false
    ) {
        screenStateHolder.currentScreenVariant.value = screen
        Log.e("select screen", currentScreenVariant.value.toString())
        if (screen == CurrentScreenVariant.SONG_LIST && !isBackFromSong) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = songRepo
                .getArtists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    screenStateHolder.artistList.value = it
                }
            actions.onArtistSelected(currentArtist.value)
        }
        if (screen == CurrentScreenVariant.FAVORITE) {
            getArtistsDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            getArtistsDisposable = songRepo
                .getArtists()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    screenStateHolder.artistList.value = it
                }
            actions.onArtistSelected(ARTIST_FAVORITE)
        }
        if (screen == CurrentScreenVariant.CLOUD_SEARCH && !isBackFromSong) {
            actions.onCloudSearchScreenSelected()
        }
    }

    fun setAppWasUpdated(value: Boolean) {
        screenStateHolder.appWasUpdated.value = value
    }

    fun addSongsFromDir() {
        actions.onAddSongsFromDir()
    }

    private fun showUploadOfferForDir(artist: String, songs: List<Song>) {
        _uploadArtist.value = artist
        _uploadSongList.value = songs
        _showUploadDialogForDir.value = true
    }

    fun hideUploadOfferForDir() {
        _showUploadDialogForDir.value = false
    }

    private fun showUploadOfferForSong(song: Song) {
        Log.e("upload", "show offer")
        _newSong.value = song
        _showUploadDialogForSong.value = true
    }

    fun hideUploadOfferForSong() {
        Log.e("upload", "hide offer")
        _showUploadDialogForSong.value = false
    }

    fun uploadListToCloud() {
        uploadListDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        uploadListDisposable = songBookAPIAdapter
            .addSongList(uploadSongList.value, userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                when (result.status) {
                    STATUS_SUCCESS -> {
                        result.data?.apply {
                            val toastText = context.getString(
                                R.string.toast_upload_songs_result,
                                success, duplicate, error
                            )
                            showToast(toastText)
                            actions.onArtistSelected(uploadArtist.value)
                            selectScreen(CurrentScreenVariant.SONG_LIST)
                        }
                    }
                    STATUS_ERROR -> showToast(result.message ?: "")
                }
            }, { error ->
                error.printStackTrace()
                showToast(R.string.error_in_app)
            })
    }

    fun uploadNewToCloud() {
        newSong.value?.apply {
            uploadSongDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            uploadSongDisposable = songBookAPIAdapter
                .addSong(this, userInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            showToast(R.string.toast_upload_to_cloud_success)
                            showNewSong()
                        }
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun showNewSong() {
        Log.e("show", "new song")
        newSong.value?.apply {
            actions.onSongByArtistAndTitleSelected(this.artist, this.title)
        }
    }

    fun copySongsFromDirToRepoWithPickedDir(pickedDir: DocumentFile) {
        if (!pickedDir.exists()) {
            showToast(R.string.toast_folder_does_not_exist)
        } else if (!pickedDir.isDirectory) {
            showToast(R.string.toast_this_is_not_folder)
        } else {
            val (artist, songs) = fileSystemAdapter.getSongsFromDir(pickedDir) {
                val toastText = context.getString(R.string.toast_file_not_found, it)
                showToast(toastText)
            }
            val actualSongs = songRepo.insertReplaceUserSongs(songs)
            val toastText = context.getString(R.string.toast_added_songs_count, songs.size)
            showToast(toastText)
            showUploadOfferForDir(artist, actualSongs)
        }
    }

    fun copySongsFromDirToRepoWithPath(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            showToast(R.string.toast_folder_does_not_exist)
        } else if (!dir.isDirectory) {
            showToast(R.string.toast_this_is_not_folder)
        } else {
            val (artist, songs) = fileSystemAdapter.getSongsFromDir(dir) {
                val toastText = context.getString(R.string.toast_file_not_found, it)
                showToast(toastText)
            }
            val actualSongs = songRepo.insertReplaceUserSongs(songs)
            val toastText = context.getString(R.string.toast_added_songs_count, songs.size)
            showToast(toastText)
            showUploadOfferForDir(artist, actualSongs)
        }
    }

    fun addSongToRepo(artist: String, title: String, text: String) {
        val song = Song()
        song.artist = artist
        song.title = title
        song.text = text
        val actualSong = songRepo.insertReplaceUserSong(song)
        showToast(R.string.toast_song_added)
        showUploadOfferForSong(actualSong)
    }

    fun purchaseItem(sku: String) {
        actions.onPurchaseItem(sku)
    }

    fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        val toastText = context.getString(resId)
        showToast(toastText)
    }

    fun openYandexMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openYandexMusicLocal(dontAskMore)
            is Cloud -> openYandexMusicCloud(dontAskMore)
        }
    }

    fun openVkMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openVkMusicLocal(dontAskMore)
            is Cloud -> openVkMusicCloud(dontAskMore)
        }
    }

    fun openYoutubeMusic(dontAskMore: Boolean) {
        when (this) {
            is Local -> openYoutubeMusicLocal(dontAskMore)
            is Cloud -> openYoutubeMusicCloud(dontAskMore)
        }
    }

    fun sendWarning(comment: String) {
        when (this) {
            is Local -> sendWarningLocal(comment)
            is Cloud -> sendWarningCloud(comment)
        }
    }
}