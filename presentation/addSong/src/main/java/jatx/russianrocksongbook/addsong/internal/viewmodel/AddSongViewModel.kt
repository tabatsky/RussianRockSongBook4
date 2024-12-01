package jatx.russianrocksongbook.addsong.internal.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.models.local.Song
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.R
import jatx.russianrocksongbook.commonviewmodel.UIAction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
internal class AddSongViewModel @Inject constructor(
    private val addSongStateHolder: AddSongStateHolder,
    addSongViewModelDeps: AddSongViewModelDeps
): CommonViewModel(
    addSongStateHolder.appStateHolder,
    addSongViewModelDeps.commonViewModelDeps
) {
    companion object {
        private const val key = "AddSong"

        @Composable
        fun getInstance(): AddSongViewModel {
            storage[key] = hiltViewModel<AddSongViewModel>()
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as AddSongViewModel
        }
    }

    private val insertReplaceUserSongUseCase =
        addSongViewModelDeps.insertReplaceUserSongUseCase
    private val addSongToCloudUseCase =
        addSongViewModelDeps.addSongToCloudUseCase

    private var uploadSongJob: Job? = null

    val artist = mutableStateOf("")
    val title = mutableStateOf("")
    val text = mutableStateOf("")

    val addSongStateFlow = addSongStateHolder.addSongStateFlow.asStateFlow()

    override fun handleAction(action: UIAction) {
        when (action) {
            is Reset -> reset()
            is HideUploadOfferForSong -> hideUploadOfferForSong()
            is AddSongToRepo -> addSongToRepo(action.artist, action.title, action.text)
            is UploadNewToCloud -> uploadNewToCloud()
            is ShowNewSong -> showNewSong()
            else -> super.handleAction(action)
        }
    }

    private fun reset() {
        artist.value = ""
        title.value = ""
        text.value = ""
    }

    private fun showUploadOfferForSong(song: Song) {
        Log.e("upload", "show offer")

        addSongStateHolder.addSongStateFlow.update {
            it.copy(showUploadDialogForSong = true, newSong = song)
        }
    }

    private fun hideUploadOfferForSong() {
        Log.e("upload", "hide offer")
        addSongStateHolder.addSongStateFlow.update {
            it.copy(showUploadDialogForSong = false)
        }
    }

    private fun addSongToRepo(artist: String, title: String, text: String) {
        val song = Song(
            artist = artist,
            title = title,
            text = text
        )
        val actualSong = insertReplaceUserSongUseCase.execute(song)
        showToast(R.string.toast_song_added)
        showUploadOfferForSong(actualSong)
    }

    private fun uploadNewToCloud() {
        addSongStateFlow.value.newSong?.let { song ->
            uploadSongJob?.let {
                if (!it.isCancelled) it.cancel()
            }
            uploadSongJob = viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            addSongToCloudUseCase.execute(song)
                        }
                        when (result.status) {
                            STATUS_SUCCESS -> {
                                showToast(R.string.toast_upload_to_cloud_success)
                                showNewSong()
                            }
                            STATUS_ERROR -> showToast(result.message ?: "")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast(R.string.error_in_app)
                    }
                }
            }
        }
    }

    private fun showNewSong() {
        Log.e("show", "new song")
        addSongStateFlow.value.newSong?.let {
            callbacks.onSongByArtistAndTitleSelected(it.artist, it.title)
        }
    }
}