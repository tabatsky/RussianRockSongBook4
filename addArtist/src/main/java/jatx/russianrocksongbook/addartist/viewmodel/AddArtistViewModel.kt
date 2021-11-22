package jatx.russianrocksongbook.addartist.viewmodel

import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.R
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddArtistViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val addArtistScreenStateHolder: AddArtistScreenStateHolder
): MvvmViewModel(
    viewModelParam,
    addArtistScreenStateHolder.screenStateHolder
) {
    val showUploadDialogForDir = addArtistScreenStateHolder
        .showUploadDialogForDir.asStateFlow()
    val uploadArtist = addArtistScreenStateHolder
        .uploadArtist.asStateFlow()
    val uploadSongList = addArtistScreenStateHolder
        .uploadSongList.asStateFlow()

    private var uploadListDisposable: Disposable? = null

    private fun showUploadOfferForDir(artist: String, songs: List<Song>) {
        addArtistScreenStateHolder.uploadArtist.value = artist
        addArtistScreenStateHolder.uploadSongList.value = songs
        addArtistScreenStateHolder.showUploadDialogForDir.value = true
    }

    fun hideUploadOfferForDir() {
        addArtistScreenStateHolder.showUploadDialogForDir.value = false
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
                            callbacks.onArtistSelected(uploadArtist.value)
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

    fun addSongsFromDir() {
        callbacks.onAddSongsFromDir()
    }
}