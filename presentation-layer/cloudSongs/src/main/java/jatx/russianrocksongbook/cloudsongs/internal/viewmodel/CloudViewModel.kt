package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.cloudsongs.internal.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.internal.paging.CloudSongSource
import jatx.russianrocksongbook.domain.models.cloud.CloudSong
import jatx.russianrocksongbook.domain.repository.cloud.OrderBy
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_ERROR
import jatx.russianrocksongbook.domain.repository.cloud.result.STATUS_SUCCESS
import jatx.russianrocksongbook.viewmodel.CommonViewModel
import jatx.russianrocksongbook.viewmodel.contracts.SongTextViewModelContract
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class CloudViewModel @Inject constructor(
    private val cloudStateHolder: CloudStateHolder,
    cloudViewModelDeps: CloudViewModelDeps
): CommonViewModel(
    cloudStateHolder.commonStateHolder,
    cloudViewModelDeps.commonViewModelDeps
), SongTextViewModelContract {
    private val addSongFromCloudUseCase =
        cloudViewModelDeps.addSongFromCloudUseCase
    private val pagedSearchUseCase =
        cloudViewModelDeps.pagedSearchUseCase
    private val voteUseCase =
        cloudViewModelDeps.voteUseCase
    private val deleteFromCloudUseCase =
        cloudViewModelDeps.deleteFromCloudUseCase
    private val addWarningCloudUseCase =
        cloudViewModelDeps.addWarningCloudUseCase

    private val cloudSongCount = cloudStateHolder.cloudSongCount.asStateFlow()
    private val cloudSong = cloudStateHolder.cloudSong.asStateFlow()

    val searchState = cloudStateHolder.searchState.asStateFlow()
    val cloudSongPosition = cloudStateHolder.cloudSongPosition.asStateFlow()

    val scrollPosition = cloudStateHolder.scrollPosition.asStateFlow()
    var isLastOrientationPortrait = cloudStateHolder.isLastOrientationPortrait.asStateFlow()
    val wasOrientationChanged = cloudStateHolder.wasOrientationChanged.asStateFlow()
    val needScroll = cloudStateHolder.needScroll.asStateFlow()

    val cloudSongsFlow = cloudStateHolder.cloudSongsFlow.asStateFlow()

    val searchFor = cloudStateHolder.searchFor.asStateFlow()
    val orderBy = cloudStateHolder.orderBy.asStateFlow()

    val invalidateCounter = cloudStateHolder.invalidateCounter.asStateFlow()

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        updateSearchState(SearchState.LOADING)
        updateScrollPosition(0)
        updateNeedScroll(true)
        updateSearchFor(searchFor)
        updateOrderBy(orderBy)
        cloudStateHolder.cloudSongsFlow.value =
            Pager(CONFIG) {
                CloudSongSource(
                    pagedSearchUseCase = pagedSearchUseCase,
                    searchFor = searchFor,
                    orderBy = orderBy,
                    onFetchDataError = { updateSearchState(SearchState.ERROR) },
                    onEmptyList = { updateSearchState(SearchState.EMPTY) }
                )
            }.flow.cachedIn(viewModelScope)
    }

    fun updateSearchState(searchState: SearchState) {
        cloudStateHolder.searchState.value = searchState
    }

    fun updateScrollPosition(position: Int) {
        cloudStateHolder.scrollPosition.value = position
    }

    fun updateOrientationWasChanged(value: Boolean) {
        cloudStateHolder.wasOrientationChanged.value = value
        if (value) updateNeedScroll(true)
    }

    fun updateNeedScroll(value: Boolean) {
        cloudStateHolder.needScroll.value = value
    }

    fun updateLastOrientationIsPortrait(value: Boolean) {
        cloudStateHolder.isLastOrientationPortrait.value = value
    }

    fun updateCloudSong(cloudSong: CloudSong?) {
        cloudStateHolder.cloudSong.value = cloudSong
    }

    fun updateCloudSongCount(count: Int) {
        cloudStateHolder.cloudSongCount.value = count
    }

    fun updateSearchFor(searchFor: String) {
        cloudStateHolder.searchFor.value = searchFor
    }

    fun updateOrderBy(orderBy: OrderBy) {
        cloudStateHolder.orderBy.value = orderBy
    }

    fun selectCloudSong(position: Int) {
        cloudStateHolder.cloudSongPosition.value = position
        updateScrollPosition(position)
        updateNeedScroll(true)
    }

    fun nextCloudSong() {
        if (cloudSongPosition.value + 1 < cloudSongCount.value)
            selectCloudSong(cloudSongPosition.value + 1)
    }

    fun prevCloudSong() {
        if (cloudSongPosition.value > 0)
            selectCloudSong(cloudSongPosition.value - 1)
    }

    @SuppressLint("CheckResult")
    fun deleteCurrentFromCloud(secret1: String, secret2: String) {
        cloudSong.value?.let {
            deleteFromCloudUseCase
                .execute(secret1, secret2, it)
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

    fun downloadCurrent() {
        cloudSong.value?.let {
            addSongFromCloudUseCase.execute(it)
            showToast(R.string.toast_chords_saved_and_added_to_favorite)
        }
    }

    @SuppressLint("CheckResult")
    fun voteForCurrent(voteValue: Int) {
        cloudSong.value?.let { cloudSong ->
            voteUseCase
                .execute(cloudSong, voteValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            if (voteValue == 1) {
                                cloudSong.likeCount += 1
                            } else if (voteValue == -1) {
                                cloudSong.dislikeCount += 1
                            }
                            cloudStateHolder.invalidateCounter.value += 1
                            showToast(R.string.toast_vote_success)
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

    override fun openVkMusicImpl(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        cloudSong.value?.let {
            callbacks.onOpenVkMusic("${it.artist} ${it.title}")
        }
    }

    override fun openYandexMusicImpl(dontAskMore: Boolean) {
        settings.yandexMusicDontAsk = dontAskMore
        cloudSong.value?.let {
            callbacks.onOpenYandexMusic("${it.artist} ${it.title}")
        }
    }

    override fun openYoutubeMusicImpl(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        cloudSong.value?.let {
            callbacks.onOpenYoutubeMusic("${it.artist} ${it.title}")
        }
    }

    override fun sendWarningImpl(comment: String) {
        cloudSong.value?.let {
            addWarningCloudUseCase
                .execute(it, comment)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> showToast(R.string.toast_send_warning_success)
                        STATUS_ERROR -> showToast(result.message ?: "")
                    }
                }, { error ->
                    error.printStackTrace()
                    showToast(R.string.error_in_app)
                })
        }
    }
}