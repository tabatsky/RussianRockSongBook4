package jatx.russianrocksongbook.addartist.internal.viewmodel

import androidx.compose.runtime.Composable
import androidx.documentfile.provider.DocumentFile
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class AddArtistViewModel @Inject constructor(
    private val addArtistStateHolder: AddArtistStateHolder,
    addArtistViewModelDeps: AddArtistViewModelDeps
): CommonViewModel(
    addArtistStateHolder.appStateHolder,
    addArtistViewModelDeps.commonViewModelDeps
) {
    companion object {
        private const val key = "AddArtist"

        @Composable
        fun getInstance(): AddArtistViewModel {
            storage[key] = hiltViewModel<AddArtistViewModel>()
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as AddArtistViewModel
        }

        fun getStoredInstance() = storage[key] as? AddArtistViewModel
    }

    private val insertReplaceUserSongsUseCase =
        addArtistViewModelDeps.insertReplaceUserSongsUseCase
    private val addSongListToCloudUseCase =
        addArtistViewModelDeps.addSongListToCloudUseCase
    private val fileSystemAdapter =
        addArtistViewModelDeps.fileSystemRepository

    val addArtistStateFlow = addArtistStateHolder
        .addArtistStateFlow.asStateFlow()

    private var uploadListJob: Job? = null

    override fun handleAction(action: UIAction) {
        when (action) {
            is HideUploadOfferForDir -> hideUploadOfferForDir()
            is UploadListToCloud -> uploadListToCloud()
            is ShowNewArtist -> showNewArtist(action.artist)
            is AddSongsFromDir -> addSongsFromDir()
            is CopySongsFromDirToRepoWithPickedDir ->
                copySongsFromDirToRepoWithPickedDir(action.pickedDir)
            is CopySongsFromDirToRepoWithPath ->
                copySongsFromDirToRepoWithPath(action.path)
            else -> super.handleAction(action)
        }
    }

    private fun showUploadOfferForDir(artist: String, songs: List<Song>) {
        addArtistStateHolder.addArtistStateFlow.update {
            it.copy(
                showUploadDialogForDir = true,
                newArtist = artist,
                uploadSongList = songs
            )
        }
    }

    private fun hideUploadOfferForDir() {
        addArtistStateHolder.addArtistStateFlow.update {
            it.copy(showUploadDialogForDir = false)
        }
    }

    private fun uploadListToCloud() {
        uploadListJob?.let {
            if (!it.isCancelled) it.cancel()
        }
        uploadListJob = viewModelScope.launch {
            withContext(Dispatchers.Main) {
                try {
                    val result = withContext(Dispatchers.IO) {
                        addSongListToCloudUseCase
                            .execute(addArtistStateFlow.value.uploadSongList)
                    }
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            result.data?.let {
                                val toastText = resources.getString(
                                    R.string.toast_upload_songs_result,
                                    it.success, it.duplicate, it.error
                                )
                                showToast(toastText)
                                callbacks.onArtistSelected(addArtistStateFlow.value.newArtist)
                            }
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

    private fun showNewArtist(artist: String) {
        callbacks.onArtistSelected(artist)
    }

    private fun addSongsFromDir() {
        callbacks.onAddSongsFromDir()
    }

    private fun copySongsFromDirToRepoWithPickedDir(pickedDir: DocumentFile) {
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

    private fun copySongsFromDirToRepoWithPath(path: String) {
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
}