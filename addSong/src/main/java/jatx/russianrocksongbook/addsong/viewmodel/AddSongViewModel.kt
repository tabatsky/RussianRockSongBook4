package jatx.russianrocksongbook.addsong.viewmodel

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.domain.Song
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.R
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AddSongViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val addSongStateHolder: AddSongStateHolder
): MvvmViewModel(
    viewModelParam,
    addSongStateHolder.commonStateHolder
) {
    val showUploadDialogForSong = addSongStateHolder
        .showUploadDialogForSong.asStateFlow()
    val newSong = addSongStateHolder
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
            callbacks.onSongByArtistAndTitleSelected(this.artist, this.title)
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
}