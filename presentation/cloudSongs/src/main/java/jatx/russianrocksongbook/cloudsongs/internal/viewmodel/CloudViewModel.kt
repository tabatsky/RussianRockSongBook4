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
import jatx.russianrocksongbook.navigation.ScreenVariant
import jatx.spinner.SpinnerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
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

    val cloudStateFlow = cloudStateHolder.cloudStateFlow.asStateFlow()

    override val currentMusic: Music?
        get() = cloudStateFlow.value.currentCloudSong

    override val currentWarnable: Warnable?
        get() = cloudStateFlow.value.currentCloudSong

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
            is UpdateCloudSongListScrollPosition -> updateScrollPosition(action.position)
            is UpdateCloudSongListNeedScroll -> updateNeedScroll(action.needScroll)
            is NextCloudSong -> nextCloudSong()
            is PrevCloudSong -> prevCloudSong()
            is DeleteCurrentFromCloud -> deleteCurrentFromCloud(action.secret1, action.secret2)
            is DownloadCurrent -> downloadCurrent()
            is VoteForCurrent -> voteForCurrent(action.voteValue)
            else -> super.handleAction(action)
        }
    }

    private fun performCloudSearch(searchFor: String, orderBy: CloudSearchOrderBy) {
        Log.e("cloudSearch", "$searchFor $orderBy")

        updateSearchState(SearchState.LOADING)
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
                    onEmptyList = { updateSearchState(SearchState.EMPTY) }
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
        cloudStateHolder.cloudStateFlow.update {
            it.copy(searchState = searchState)
        }
    }

    private fun updateSearchFor(searchFor: String) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(searchFor = searchFor)
        }
    }

    private fun updateOrderBy(orderBy: CloudSearchOrderBy) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(orderBy = orderBy)
        }
    }

    private fun updateCurrentCloudSongCount(count: Int) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(currentCloudSongCount = count)
        }
    }

    private fun updateCurrentCloudSong(cloudSong: CloudSong?) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(currentCloudSong = cloudSong)
        }
    }

    private fun selectCloudSong(position: Int) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(cloudSongPosition = position, scrollPosition = position, needScroll = true)
        }
    }

    private fun updateScrollPosition(position: Int) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(scrollPosition = position)
        }
    }

    private fun updateNeedScroll(needScroll: Boolean) {
        cloudStateHolder.cloudStateFlow.update {
            it.copy(needScroll = needScroll)
        }
    }

    private fun nextCloudSong() {
        val currentPosition = cloudStateFlow.value.cloudSongPosition
        val songCount = cloudStateFlow.value.currentCloudSongCount
        if (currentPosition + 1 < songCount)
            selectScreen(
                ScreenVariant
                    .CloudSongText(currentPosition + 1))
    }

    private fun prevCloudSong() {
        val currentPosition = cloudStateFlow.value.cloudSongPosition
        if (currentPosition > 0)
            selectScreen(
                ScreenVariant
                    .CloudSongText(currentPosition - 1))
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
        cloudStateHolder.cloudStateFlow.update {
            it.copy(cloudSongsFlow = flow)
        }
    }
}