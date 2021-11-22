package jatx.russianrocksongbook.localsongs.viewmodel

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.data.*
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.*
import jatx.russianrocksongbook.viewmodel.interfaces.Local
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val localScreenStateHolder: LocalScreenStateHolder
): MvvmViewModel(
    viewModelParam,
    localScreenStateHolder.screenStateHolder
), Local {

    val currentSongCount = localScreenStateHolder.currentSongCount.asStateFlow()
    val currentSongList = localScreenStateHolder.currentSongList.asStateFlow()
    val currentSongPosition = localScreenStateHolder.currentSongPosition.asStateFlow()
    val currentSong = localScreenStateHolder.currentSong.asStateFlow()

    val isEditorMode = localScreenStateHolder.isEditorMode.asStateFlow()
    val isAutoPlayMode = localScreenStateHolder.isAutoPlayMode.asStateFlow()
    val isUploadButtonEnabled = localScreenStateHolder.isUploadButtonEnabled.asStateFlow()

    private var showSongsDisposable: Disposable? = null
    private var selectSongDisposable: Disposable? = null
    private var uploadSongDisposable: Disposable? = null
    private var sendWarningDisposable: Disposable? = null

    fun updateArtistList(list: List<String>) {
        localScreenStateHolder.screenStateHolder.artistList.value = list
    }

    fun selectArtist(
        artist: String,
        forceOnSuccess: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        Log.e("select artist", artist)
        showSongsDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        when (artist) {
            ARTIST_ADD_ARTIST -> {
                selectScreen(CurrentScreenVariant.ADD_ARTIST)
            }
            ARTIST_ADD_SONG -> {
                selectScreen(CurrentScreenVariant.ADD_SONG)
            }
            ARTIST_CLOUD_SONGS -> {
                selectScreen(CurrentScreenVariant.CLOUD_SEARCH)
            }
            ARTIST_DONATION -> {
                selectScreen(CurrentScreenVariant.DONATION)
            }
            else -> {
                localScreenStateHolder
                    .screenStateHolder
                    .currentArtist.value = artist
                localScreenStateHolder.currentSongCount.value = songRepo.getCountByArtist(artist)
                showSongsDisposable = songRepo
                    .getSongsByArtist(artist)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val oldArtist = currentSongList.value.getOrNull(0)?.artist
                        val newArtist = it.getOrNull(0)?.artist
                        localScreenStateHolder.currentSongList.value = it
                        if (oldArtist != newArtist || forceOnSuccess) {
                            onSuccess()
                        }
                    }
            }
        }
    }

    fun selectSong(position: Int) {
        Log.e("select song", position.toString())
        localScreenStateHolder.currentSongPosition.value = position
        localScreenStateHolder.isAutoPlayMode.value = false
        localScreenStateHolder.isEditorMode.value = false
        selectSongDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        selectSongDisposable = songRepo
            .getSongByArtistAndPosition(currentArtist.value, position)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                localScreenStateHolder.currentSong.value = it
            }
    }

    fun nextSong() {
        if (currentSongCount.value > 0) {
            selectSong((currentSongPosition.value + 1) % currentSongCount.value)
        }
    }

    fun prevSong() {
        if (currentSongCount.value > 0) {
            if (currentSongPosition.value > 0) {
                selectSong((currentSongPosition.value - 1) % currentSongCount.value)
            } else {
                selectSong(currentSongCount.value - 1)
            }
        }
    }

    fun saveSong(song: Song) {
        songRepo.updateSong(song)
    }

    fun setEditorMode(value: Boolean) {
        localScreenStateHolder.isEditorMode.value = value
    }

    fun setAutoPlayMode(value: Boolean) {
        localScreenStateHolder.isAutoPlayMode.value = value
    }

    fun setFavorite(value: Boolean) {
        Log.e("set favorite", value.toString())
        currentSong.value?.apply {
            this.favorite = value
            saveSong(this)
            if (!value && currentArtist.value == ARTIST_FAVORITE) {
                localScreenStateHolder.currentSongCount.value = songRepo.getCountByArtist(
                    ARTIST_FAVORITE
                )
                if (currentSongCount.value > 0) {
                    if (currentSongPosition.value >= currentSongCount.value) {
                        selectSong(currentSongPosition.value - 1)
                    } else {
                        selectSong(currentSongPosition.value)
                    }
                } else {
                    back {}
                }
            }
            if (value) {
                showToast(R.string.toast_added_to_favorite)
            } else {
                showToast(R.string.toast_removed_from_favorite)
            }
        }
    }

    fun deleteCurrentToTrash() {
        currentSong.value?.apply {
            songRepo.deleteSongToTrash(this)
            localScreenStateHolder.currentSongCount.value = songRepo.getCountByArtist(currentArtist.value)
            if (currentSongCount.value > 0) {
                if (currentSongPosition.value >= currentSongCount.value) {
                    selectSong(currentSongPosition.value - 1)
                } else {
                    selectSong(currentSongPosition.value)
                }
            } else {
                back {}
            }
        }
        showToast(R.string.toast_deleted_to_trash)
    }

    override fun openYandexMusicLocal(dontAskMore: Boolean) {
        settings.yandexMusicDontAsk = dontAskMore
        currentSong.value?.apply {
            callbacks.onOpenYandexMusic("$artist $title")
        }
    }

    override fun openVkMusicLocal(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        currentSong.value?.apply {
            callbacks.onOpenVkMusic("$artist $title")
        }
    }

    override fun openYoutubeMusicLocal(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        currentSong.value?.apply {
            callbacks.onOpenYoutubeMusic("$artist $title")
        }
    }

    override fun sendWarningLocal(comment: String) {
        currentSong.value?.apply {
            sendWarningDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            sendWarningDisposable = songBookAPIAdapter
                .addWarning(this, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_send_warning_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun uploadCurrentToCloud() {
        currentSong.value?.apply {
            localScreenStateHolder.isUploadButtonEnabled.value = false
            uploadSongDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            uploadSongDisposable = songBookAPIAdapter
                .addSong(this, userInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_upload_to_cloud_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                    localScreenStateHolder.isUploadButtonEnabled.value = true
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                    localScreenStateHolder.isUploadButtonEnabled.value = true
                })
        }
    }

    fun reviewApp() {
        callbacks.onReviewApp()
    }

    fun showDevSite() {
        callbacks.onShowDevSite()
    }
}