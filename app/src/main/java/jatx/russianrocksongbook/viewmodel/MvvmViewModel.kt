package jatx.russianrocksongbook.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.documentfile.provider.DocumentFile
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.R
import jatx.russianrocksongbook.data.*
import jatx.russianrocksongbook.data.FileSystemAdapter
import jatx.russianrocksongbook.data.gson.STATUS_ERROR
import jatx.russianrocksongbook.data.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.domain.Song
import jatx.russianrocksongbook.domain.formatRating
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MvvmViewModel(
    val context: Context,
    val songRepo: SongRepository,
    val settings: Settings,
    val songBookAPIAdapter: SongBookAPIAdapter,
    val userInfo: UserInfo,
    val fileSystemAdapter: FileSystemAdapter
    ) {
    var onOpenVkMusic: (String) -> Unit = {}
    var onOpenYoutubeMusic: (String) -> Unit = {}
    var onShowDevSite: () -> Unit = {}
    var onReviewApp: () -> Unit = {}
    var onRestartApp: () -> Unit = {}
    var onAddSongsFromDir: () -> Unit = {}
    var onPurchaseItem: (String) -> Unit = {}


    private val _currentScreen = MutableStateFlow(CurrentScreen.STUB)
    val currentScreen = _currentScreen.asStateFlow()
    private val _appWasUpdated = MutableStateFlow(false)
    val appWasUpdated = _appWasUpdated.asStateFlow()

    private val _stubProgressCurrent = MutableStateFlow(0)
    val stubCurrentProgress = _stubProgressCurrent.asStateFlow()
    private val _stubTotalProgress = MutableStateFlow(100)
    val stubTotalProgress = _stubTotalProgress.asStateFlow()

    private val _currentArtist = MutableStateFlow(settings.defaultArtist)
    val currentArtist = _currentArtist.asStateFlow()
    private val _artistList = MutableStateFlow(listOf<String>())
    val artistList = _artistList.asStateFlow()
    private val _currentSongCount = MutableStateFlow(0)
    val currentSongCount = _currentSongCount.asStateFlow()
    private val _currentSongList = MutableStateFlow(listOf<Song>())
    val currentSongList = _currentSongList.asStateFlow()
    private val _currentSongPosition = MutableStateFlow(0)
    val currentSongPosition = _currentSongPosition.asStateFlow()
    private val _currentSong: MutableStateFlow<Song?> = MutableStateFlow(null)
    val currentSong = _currentSong.asStateFlow()
    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()
    private val _cloudSongCount = MutableStateFlow(0)
    val cloudSongCount = _cloudSongCount.asStateFlow()
    private val _cloudSongList = MutableStateFlow(listOf<CloudSong>())
    val cloudSongList = _cloudSongList.asStateFlow()
    private val _cloudSongPosition = MutableStateFlow(0)
    val cloudSongPosition = _cloudSongPosition.asStateFlow()
    private val _cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSong = _cloudSong.asStateFlow()
    private val _isEditorMode = MutableStateFlow(false)
    val isEditorMode = _isEditorMode.asStateFlow()
    private val _isAutoPlayMode = MutableStateFlow(false)
    val isAutoPlayMode = _isAutoPlayMode.asStateFlow()
    private val _isUploadButtonEnabled = MutableStateFlow(true)
    val isUploadButtonEnabled = _isUploadButtonEnabled.asStateFlow()
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

    private var showSongsDisposable: Disposable? = null
    private var selectSongDisposable: Disposable? = null

    fun back(onFinish: () -> Unit) {
        Log.e("current screen", currentScreen.value.toString())
        when (currentScreen.value) {
            CurrentScreen.STUB, CurrentScreen.SONG_LIST -> {
                onFinish()
            }
            CurrentScreen.CLOUD_SONG_TEXT -> {
                selectScreen(CurrentScreen.CLOUD_SEARCH, true)
            }
            else -> {
                selectScreen(CurrentScreen.SONG_LIST, true)
            }
        }
    }

    fun selectScreen(screen: CurrentScreen, isBack: Boolean = false) {
        _currentScreen.value = screen
        Log.e("select screen", currentScreen.value.toString())
        if (screen == CurrentScreen.SONG_LIST && !isBack) {
            songRepo
                .getArtistsFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _artistList.value = it
                }
            selectArtist(currentArtist.value) {}
        }
        if (screen == CurrentScreen.FAVORITE) {
            songRepo
                .getArtistsFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    _artistList.value = it
                }
            selectArtist(ARTIST_FAVORITE) {}
        }
        if (screen == CurrentScreen.CLOUD_SEARCH && !isBack) {
            cloudSearch("", OrderBy.BY_ID_DESC)
            selectCloudSong(0)
        }
    }

    fun setAppWasUpdated(value: Boolean) {
        _appWasUpdated.value = value
    }

    fun updateStubProgress(current: Int, total: Int) {
        _stubProgressCurrent.value = current
        _stubTotalProgress.value = total
    }

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        _isCloudLoading.value = true
        songBookAPIAdapter
            .searchSongs(searchFor, orderBy)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                _isCloudLoading.value = false
                when (result.status) {
                    STATUS_ERROR -> showToast(result.message ?: "")
                    STATUS_SUCCESS -> {
                        result.data?.apply {
                            _cloudSongList.value = this.map{ CloudSong(it) }
                            _cloudSongCount.value = this.size
                        }
                    }
                }
            }, { error ->
                error.printStackTrace()
                _isCloudLoading.value = false
                showToast(R.string.error_in_app)
            })
    }

    fun selectArtist(
        artist: String,
        forceOnSuccess: Boolean = false,
        onSuccess: () -> Unit
    ) {
        println("selected: $artist")
        showSongsDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        when (artist) {
            ARTIST_ADD_ARTIST -> {
                selectScreen(CurrentScreen.ADD_ARTIST)
            }
            ARTIST_ADD_SONG -> {
                selectScreen(CurrentScreen.ADD_SONG)
            }
            ARTIST_CLOUD_SONGS -> {
                selectScreen(CurrentScreen.CLOUD_SEARCH)
            }
            ARTIST_DONATION -> {
                selectScreen(CurrentScreen.DONATION)
            }
            else -> {
                _currentArtist.value = artist
                _currentSongCount.value = songRepo.getCountByArtist(artist)
                showSongsDisposable = songRepo
                    .getSongsByArtist(artist)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        val oldArtist = currentSongList.value.getOrNull(0)?.artist
                        val newArtist = it.getOrNull(0)?.artist
                        _currentSongList.value = it
                        if (oldArtist != newArtist || forceOnSuccess) {
                            onSuccess()
                        }
                    }
            }
        }
    }

    fun selectSong(position: Int) {
        Log.e("select song", position.toString())
        _currentSongPosition.value = position
        _isAutoPlayMode.value = false
        _isEditorMode.value = false
        selectSongDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        selectSongDisposable = songRepo
            .getSongByArtistAndPosition(currentArtist.value, position)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                _currentSong.value = it
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

    fun selectCloudSong(position: Int) {
        _cloudSongPosition.value = position
        _cloudSong.value = cloudSongList.value.getOrNull(position)
    }

    fun nextCloudSong() {
        if (cloudSongCount.value > 0) {
            selectCloudSong((cloudSongPosition.value + 1) % cloudSongCount.value)
        }
    }

    fun prevCloudSong() {
        if (cloudSongCount.value > 0) {
            if (cloudSongPosition.value > 0) {
                selectCloudSong((cloudSongPosition.value - 1) % cloudSongCount.value)
            } else {
                selectCloudSong(cloudSongCount.value - 1)
            }
        }
    }

    fun setEditorMode(value: Boolean) {
        _isEditorMode.value = value
    }

    fun setAutoPlayMode(value: Boolean) {
        _isAutoPlayMode.value = value
    }

    fun setFavorite(value: Boolean) {
        Log.e("set favorite", value.toString())
        currentSong.value?.apply {
            this.favorite = value
            saveSong(this)
            if (!value && currentArtist.value == ARTIST_FAVORITE) {
                _currentSongCount.value = songRepo.getCountByArtist(ARTIST_FAVORITE)
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
            _currentSongCount.value = songRepo.getCountByArtist(currentArtist.value)
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

    fun downloadCurrent() {
        cloudSong.value?.apply {
            songRepo.addSongFromCloud(this)
            showToast(R.string.chords_saved_and_added_to_favorite)
        }
    }

    fun voteForCurrent(voteValue: Int) {
        cloudSong.value?.apply {
            songBookAPIAdapter
                .vote(this, userInfo, voteValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            val voteWeight = result.data?.toDouble() ?: 0.0
                            val voteWeightStr = formatRating(voteWeight)
                            showToast("Голос засчитан. Вес голоса: $voteWeightStr")
                        }
                        STATUS_ERROR -> {
                            showToast(result.message ?: "")
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun deleteCurrentFromCloud(secret1: String, secret2: String) {
        cloudSong.value?.apply {
            songBookAPIAdapter
                .delete(secret1, secret2, this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            val number = result.data ?: 0
                            showToast(number.toString())
                        }
                        STATUS_ERROR -> {
                            showToast(
                                result
                                    .message
                                    ?.replace("уй", "**")
                                    ?: ""
                            )
                        }
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun openVkMusic(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        currentSong.value?.apply {
            onOpenVkMusic("$artist $title")
        }
    }

    fun openYoutubeMusic(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        currentSong.value?.apply {
            onOpenYoutubeMusic("$artist $title")
        }
    }

    fun openVkMusicCloud(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            onOpenVkMusic("$artist $title")
        }
    }

    fun openYoutubeMusicCloud(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            onOpenYoutubeMusic("$artist $title")
        }
    }

    fun addSongsFromDir() {
        onAddSongsFromDir()
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
        songBookAPIAdapter
            .addSongList(uploadSongList.value, userInfo)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                when (result.status) {
                    STATUS_SUCCESS -> {
                        result.data?.apply {
                            val toastText = context.getString(
                                R.string.upload_songs_result,
                                success, duplicate, error
                            )
                            showToast(toastText)
                            selectArtist(uploadArtist.value) {}
                            selectScreen(CurrentScreen.SONG_LIST)
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
            songBookAPIAdapter
                .addSong(this, userInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            showToast(R.string.upload_to_cloud_success)
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
            val artist = this.artist
            selectArtist(
                artist = artist,
                forceOnSuccess = true,
                onSuccess = {
                    val position = currentSongList
                        .value
                        .map { it.title }
                        .indexOf(this.title)
                    selectSong(position)
                    selectScreen(CurrentScreen.SONG_TEXT)
                }
            )
        }
    }

    fun uploadCurrentToCloud() {
        currentSong.value?.apply {
            _isUploadButtonEnabled.value = false
            songBookAPIAdapter
                .addSong(this, userInfo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.upload_to_cloud_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                    _isUploadButtonEnabled.value = true
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                    _isUploadButtonEnabled.value = true
                })
        }
    }

    fun sendWarning(comment: String) {
        currentSong.value?.apply {
            songBookAPIAdapter
                .addWarning(this, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.send_warning_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun sendWarningCloud(comment: String) {
        cloudSong.value?.apply {
            songBookAPIAdapter
                .addWarning(this, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.send_warning_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }

    fun copySongsFromDirToRepo(pickedDir: DocumentFile) {
        if (!pickedDir.exists()) {
            val toastText = context.getString(R.string.folder_does_not_exist)
            showToast(toastText)
        } else if (!pickedDir.isDirectory) {
            val toastText = context.getString(R.string.this_is_not_folder)
            showToast(toastText)
        } else {
            val (artist, songs) = fileSystemAdapter.getSongsFromDir(pickedDir) {
                showToast("Файл не найден: $it")
            }
            val actualSongs = songRepo.insertReplaceUserSongs(songs)
            val toastText = context.getString(R.string.added_songs_count, songs.size)
            showToast(toastText)
            showUploadOfferForDir(artist, actualSongs)
        }
    }

    fun copySongsFromDirToRepo(path: String) {
        val dir = File(path)
        if (!dir.exists()) {
            val toastText = context.getString(R.string.folder_does_not_exist)
            showToast(toastText)
        } else if (!dir.isDirectory) {
            val toastText = context.getString(R.string.this_is_not_folder)
            showToast(toastText)
        } else {
            val (artist, songs) = fileSystemAdapter.getSongsFromDir(dir) {
                showToast("Файл не найден: $it")
            }
            val actualSongs = songRepo.insertReplaceUserSongs(songs)
            val toastText = context.getString(R.string.added_songs_count, songs.size)
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
        onPurchaseItem(sku)
    }

    fun showToast(toastText: String) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show()
    }

    fun showToast(@StringRes resId: Int) {
        val toastText = context.getString(resId)
        showToast(toastText)
    }
}