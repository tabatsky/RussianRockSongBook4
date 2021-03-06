package jatx.russianrocksongbook.addartist.internal.viewmodel

import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.CurrentScreenVariant
import jatx.russianrocksongbook.viewmodel.R
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class AddArtistViewModel @Inject constructor(
    private val addArtistStateHolder: AddArtistStateHolder,
    addArtistViewModelDeps: AddArtistViewModelDeps
): CommonViewModel(
    addArtistStateHolder.commonStateHolder,
    addArtistViewModelDeps.commonViewModelDeps
) {
    private val insertReplaceUserSongsUseCase =
        addArtistViewModelDeps.insertReplaceUserSongsUseCase
    private val addSongListToCloudUseCase =
        addArtistViewModelDeps.addSongListToCloudUseCase
    private val fileSystemAdapter =
        addArtistViewModelDeps.fileSystemRepository

    val showUploadDialogForDir = addArtistStateHolder
        .showUploadDialogForDir.asStateFlow()
    val uploadArtist = addArtistStateHolder
        .uploadArtist.asStateFlow()
    private val uploadSongList = addArtistStateHolder
        .uploadSongList.asStateFlow()

    private var uploadListDisposable: Disposable? = null

    private fun showUploadOfferForDir(artist: String, songs: List<Song>) {
        addArtistStateHolder.uploadArtist.value = artist
        addArtistStateHolder.uploadSongList.value = songs
        addArtistStateHolder.showUploadDialogForDir.value = true
    }

    fun hideUploadOfferForDir() {
        addArtistStateHolder.showUploadDialogForDir.value = false
    }

    fun uploadListToCloud() {
        uploadListDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        uploadListDisposable = addSongListToCloudUseCase
            .execute(uploadSongList.value)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                when (result.status) {
                    STATUS_SUCCESS -> {
                        result.data?.let {
                            val toastText = resources.getString(
                                R.string.toast_upload_songs_result,
                                it.success, it.duplicate, it.error
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
                val toastText = resources.getString(R.string.toast_file_not_found, it)
                showToast(toastText)
            }
            val actualSongs = insertReplaceUserSongsUseCase.execute(songs)
            val toastText = resources.getString(R.string.toast_added_songs_count, songs.size)
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
                val toastText = resources.getString(R.string.toast_file_not_found, it)
                showToast(toastText)
            }
            val actualSongs = insertReplaceUserSongsUseCase.execute(songs)
            val toastText = resources.getString(R.string.toast_added_songs_count, songs.size)
            showToast(toastText)
            showUploadOfferForDir(artist, actualSongs)
        }
    }

    fun addSongsFromDir() {
        callbacks.onAddSongsFromDir()
    }
}