package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.internal.paging.CloudSongSource
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.commonviewmodel.CommonViewModel
import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.models.music.Music
import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.navigation.*
import jatx.spinner.SpinnerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CloudViewModel @Inject constructor(
    private val cloudStateHolder: CloudStateHolder,
    cloudViewModelDeps: CloudViewModelDeps
): CommonViewModel(
    cloudStateHolder.appStateHolder,
    cloudViewModelDeps.commonViewModelDeps
) {
    companion object {
        private const val key = "Cloud"

        @Composable
        fun getInstance(): CloudViewModel {
            if (!storage.containsKey(key)){
                storage[key] = hiltViewModel<CloudViewModel>()
            }
            storage[key]?.launchJobsIfNecessary()
            return storage[key] as CloudViewModel
        }
    }

    private val addSongFromCloudUseCase =
        cloudViewModelDeps.addSongFromCloudUseCase
    private val pagedSearchUseCase =
        cloudViewModelDeps.pagedSearchUseCase
    private val voteUseCase =
        cloudViewModelDeps.voteUseCase
    private val deleteFromCloudUseCase =
        cloudViewModelDeps.deleteFromCloudUseCase

    val spinnerStateOrderBy = mutableStateOf(SpinnerState(0, false))

    private val allLikes = mutableStateMapOf<CloudSong, Int>()
    private val allDislikes = mutableStateMapOf<CloudSong, Int>()

    val cloudStateFlow = cloudStateHolder.cloudStateFlow

    override val currentMusic: Music?
        get() = cloudStateFlow.value.currentCloudSong

    override val currentWarnable: Warnable?
        get() = cloudStateFlow.value.currentCloudSong

    override fun resetState() = cloudStateHolder.reset()

    override fun handleAction(action: UIAction) {
        when (action) {
            is PerformCloudSearch -> performCloudSearch(action.searchFor, action.orderBy)
            is UpdateSearchState -> updateSearchState(action.searchState)
            is UpdateSearchFor -> updateSearchFor(action.searchFor)
            is UpdateOrderBy -> updateOrderBy(action.orderBy)
            is UpdateCurrentCloudSongCount -> updateCurrentCloudSongCount(action.count)
            is UpdateCurrentCloudSong -> updateCurrentCloudSong(action.cloudSong)
            is SelectCloudSong -> selectCloudSong(action.position)
            is UpdateCurrentCloudSongPosition -> updateCurrentCloudSongPosition(action.position)
            is UpdateCloudSongListScrollPosition -> updateScrollPosition(action.position)
            is UpdateCloudSongListNeedScroll -> updateNeedScroll(action.needScroll)
            is NextCloudSong -> nextCloudSong()
            is PrevCloudSong -> prevCloudSong()
            is DeleteCurrentFromCloud -> deleteCurrentFromCloud(action.secret1, action.secret2)
            is DownloadCurrent -> downloadCurrent()
            is VoteForCurrent -> voteForCurrent(action.voteValue)
            is UpdateShowVkDialog -> updateShowVkDialog(action.needShow)
            is UpdateShowYandexDialog -> updateShowYandexDialog(action.needShow)
            is UpdateShowYoutubeDialog -> updateShowYoutubeDialog(action.needShow)
            is UpdateShowWarningDialog -> updateShowWarningDialog(action.needShow)
            is UpdateShowDeleteDialog -> updateShowDeleteDialog(action.needShow)
            is UpdateShowChordDialog -> updateShowChordDialog(action.needShow, action.selectedChord)

            else -> super.handleAction(action)
        }
    }

    private fun performCloudSearch(searchFor: String, orderBy: CloudSearchOrderBy) {
        Log.e("cloudSearch", "$searchFor $orderBy")

        updateSearchState(SearchState.LOADING_FIRST_PAGE)
        updateScrollPosition(0)
        updateNeedScroll(true)
        updateSearchFor(searchFor)
        updateOrderBy(orderBy)
        allLikes.clear()
        allDislikes.clear()
        updateCloudSongsFlow(
            Pager(CONFIG) {
                CloudSongSource(
                    pagedSearchUseCase = pagedSearchUseCase,
                    searchFor = searchFor,
                    orderBy = orderBy,
                    onFetchDataError = { updateSearchState(SearchState.ERROR) },
                    onEmptyList = { updateSearchState(SearchState.EMPTY) },
                    onNoMorePages = { updateSearchState(SearchState.NO_MORE_PAGES) }
                )
            }.flow
                .cachedIn(viewModelScope)
                .combine(snapshotFlow { allLikes.values.sum() to allDislikes.values.sum() }) { pagingData, _ ->
                    pagingData.map { cloudSong ->
                        val likeCount = allLikes[cloudSong] ?: cloudSong.likeCount
                        val dislikeCount = allDislikes[cloudSong] ?: cloudSong.dislikeCount
                        cloudSong.copy(
                            likeCount = likeCount,
                            dislikeCount = dislikeCount
                        )
                    }
                }
        )
    }

    private fun updateSearchState(searchState: SearchState) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(searchState = searchState)
        changeCloudState(newState)
    }

    private fun updateSearchFor(searchFor: String) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(searchFor = searchFor)
        changeCloudState(newState)
    }

    private fun updateOrderBy(orderBy: CloudSearchOrderBy) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(orderBy = orderBy)
        changeCloudState(newState)
    }

    private fun updateCurrentCloudSongCount(count: Int) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(currentCloudSongCount = count)
        changeCloudState(newState)
    }

    private fun updateCurrentCloudSong(cloudSong: CloudSong?) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(currentCloudSong = cloudSong)
        changeCloudState(newState)
    }

    private fun selectCloudSong(position: Int) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(
            currentCloudSongPosition = position, scrollPosition = position, needScroll = true
        )
        changeCloudState(newState)
    }

    private fun updateCurrentCloudSongPosition(position: Int) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(currentCloudSongPosition = position)
        changeCloudState(newState)
    }

    private fun updateScrollPosition(position: Int) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(scrollPosition = position)
        changeCloudState(newState)
    }

    private fun updateNeedScroll(needScroll: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(needScroll = needScroll)
        changeCloudState(newState)
    }

    private fun nextCloudSong() {
        val currentPosition = cloudStateFlow.value.currentCloudSongPosition
        val songCount = cloudStateFlow.value.currentCloudSongCount
        if (currentPosition + 1 < songCount)
            selectScreen(CloudSongTextScreenVariant(currentPosition + 1))
    }

    private fun prevCloudSong() {
        val currentPosition = cloudStateFlow.value.currentCloudSongPosition
        if (currentPosition > 0)
            selectScreen(CloudSongTextScreenVariant(currentPosition - 1))
    }

    @SuppressLint("CheckResult")
    private fun deleteCurrentFromCloud(secret1: String, secret2: String) {
        cloudStateFlow.value.currentCloudSong?.let {
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            deleteFromCloudUseCase
                                .execute(secret1, secret2, it)
                        }
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast(R.string.error_in_app)
                    }
                }
            }
        }
    }

    private fun downloadCurrent() {
        cloudStateFlow.value.currentCloudSong?.let {
            addSongFromCloudUseCase.execute(it)
            showToast(R.string.toast_chords_saved_and_added_to_favorite)
        }
    }

    @SuppressLint("CheckResult")
    private fun voteForCurrent(voteValue: Int) {
        cloudStateFlow.value.currentCloudSong?.let { cloudSong ->
            viewModelScope.launch {
                withContext(Dispatchers.Main) {
                    try {
                        val result = withContext(Dispatchers.IO) {
                            voteUseCase
                                .execute(cloudSong, voteValue)
                        }
                        when (result.status) {
                            STATUS_SUCCESS -> {
                                if (voteValue == 1) {
                                    allLikes[cloudSong] = cloudSong.likeCount + 1
                                } else if (voteValue == -1) {
                                    allDislikes[cloudSong] = cloudSong.dislikeCount + 1
                                }
                                showToast(R.string.toast_vote_success)
                            }
                            STATUS_ERROR -> {
                                showToast(result.message ?: "")
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast(R.string.error_in_app)
                    }
                }
            }
        }
    }

    private fun updateCloudSongsFlow(flow: Flow<PagingData<CloudSong>>?) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(cloudSongsFlow = flow)
        changeCloudState(newState)
    }

    private fun updateShowVkDialog(needShow: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(showVkDialog = needShow)
        changeCloudState(newState)
    }

    private fun updateShowYandexDialog(needShow: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(showYandexDialog = needShow)
        changeCloudState(newState)
    }

    private fun updateShowYoutubeDialog(needShow: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(showYoutubeDialog = needShow)
        changeCloudState(newState)
    }

    private fun updateShowWarningDialog(needShow: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(showWarningDialog = needShow)
        changeCloudState(newState)
    }

    private fun updateShowDeleteDialog(needShow: Boolean) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(showDeleteDialog = needShow)
        changeCloudState(newState)
    }

    private fun updateShowChordDialog(needShow: Boolean, chord: String) {
        val cloudState = cloudStateFlow.value
        val newState = cloudState.copy(
            showChordDialog = needShow,
            selectedChord = chord
        )
        changeCloudState(newState)
    }

    private fun changeCloudState(cloudState: CloudState) {
        cloudStateHolder.changeCloudState(cloudState)
    }
}