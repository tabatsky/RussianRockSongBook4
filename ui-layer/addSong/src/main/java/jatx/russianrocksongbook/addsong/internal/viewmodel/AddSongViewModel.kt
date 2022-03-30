package jatx.russianrocksongbook.addsong.internal.viewmodel

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.domain.repository.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.R
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class AddSongViewModel @Inject constructor(
    private val addSongStateHolder: AddSongStateHolder,
    addSongViewModelDeps: AddSongViewModelDeps
): CommonViewModel(
    addSongStateHolder.commonStateHolder,
    addSongViewModelDeps.commonViewModelDeps
) {
    private val insertReplaceUserSongUseCase =
        addSongViewModelDeps.insertReplaceUserSongUseCase
    private val addSongToCloudUseCase =
        addSongViewModelDeps.addSongToCloudUseCase

    val showUploadDialogForSong = addSongStateHolder
        .showUploadDialogForSong.asStateFlow()
    private val newSong = addSongStateHolder
        .newSong.asStateFlow()

    private var uploadSongDisposable: Disposable? = null

    private fun showUploadOfferForSong(song: Song) {
        Log.e("upload", "show offer")
        addSongStateHolder.newSong.value = song
        addSongStateHolder.showUploadDialogForSong.value = true
    }

    fun hideUploadOfferForSong() {
        Log.e("upload", "hide offer")
        addSongStateHolder.showUploadDialogForSong.value = false
    }

    fun uploadNewToCloud() {
        newSong.value?.let { song ->
            uploadSongDisposable?.let {
                if (!it.isDisposed) it.dispose()
            }
            uploadSongDisposable = addSongToCloudUseCase
                .execute(song)
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
        newSong.value?.let {
            callbacks.onSongByArtistAndTitleSelected(it.artist, it.title)
        }
    }

    fun addSongToRepo(artist: String, title: String, text: String) {
        val song = Song().apply {
            this.artist = artist
            this.title = title
            this.text = text
        }
        val actualSong = insertReplaceUserSongUseCase.execute(song)
        showToast(R.string.toast_song_added)
        showUploadOfferForSong(actualSong)
    }
}