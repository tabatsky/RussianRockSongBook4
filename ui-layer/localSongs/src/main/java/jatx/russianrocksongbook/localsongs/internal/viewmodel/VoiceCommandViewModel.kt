package jatx.russianrocksongbook.localsongs.internal.viewmodel

import android.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.domain.models.Song
import jatx.russianrocksongbook.localsongs.R
import jatx.russianrocksongbook.voicecommands.api.aliases
import jatx.russianrocksongbook.voicecommands.api.voiceFilter
import javax.inject.Inject

@HiltViewModel
internal class VoiceCommandViewModel @Inject constructor(
    private val localStateHolder: LocalStateHolder,
    voiceCommandViewModelDeps: VoiceCommandViewModelDeps
): LocalViewModel(
    localStateHolder,
    voiceCommandViewModelDeps.localViewModelDeps
) {
    private val getArtistsAsListUseCase =
        voiceCommandViewModelDeps.getArtistsAsListUseCase

    private val getSongsByVoiceSearchUseCase =
        voiceCommandViewModelDeps.getSongsByVoiceSearchUseCase

    fun parseAndExecuteVoiceCommand(command: String) {
        Log.e("voice command", command)

        if (command.lowercase().startsWith("открой группу ")
                .or(command.lowercase().startsWith("открой раздел "))
        ) {
            val voiceArtist = command
                .lowercase()
                .replace("открой группу ", "")
                .replace("открой раздел ", "")
                .voiceFilter()
            val allArtists = getArtistsAsListUseCase.execute()
            val index = allArtists
                .indexOfFirst { voiceArtist.aliases().contains(it.voiceFilter()) }
            if (index < 0) {
                showToast(R.string.toast_artist_not_found)
            } else {
                selectArtist(allArtists[index]) { selectSong(0) }
            }
        } else if (command.lowercase().startsWith("открой песню ")) {
            val voiceSearch = command
                .lowercase()
                .replace("открой песню ", "")
                .voiceFilter()
            val songList = arrayListOf<Song>()
            voiceSearch.aliases().forEach {
                songList.addAll(getSongsByVoiceSearchUseCase.execute(it))
            }
            if (songList.isEmpty()) {
                showToast(R.string.toast_song_not_found)
            } else {
                val currentArtist = localStateHolder.commonStateHolder.currentArtist.value
                val currentIndex = songList
                    .indexOfFirst { it.artist.voiceFilter() == currentArtist.voiceFilter() }
                val index = if (currentIndex < 0) 0 else currentIndex

                callbacks.onSongByArtistAndTitleSelected(songList[index].artist, songList[index].title)
            }
        } else {
            showToast(R.string.toast_unknown_command)
        }
    }
}