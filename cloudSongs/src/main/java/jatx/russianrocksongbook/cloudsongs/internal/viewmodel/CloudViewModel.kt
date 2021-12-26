package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

import android.annotation.SuppressLint
import androidx.paging.Pager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.cloudsongs.internal.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.internal.paging.CloudSongSource
import jatx.russianrocksongbook.cloudsongs.internal.paging.SnapshotHolder
import jatx.russianrocksongbook.cloudsongs.R
import jatx.russianrocksongbook.networking.api.result.STATUS_ERROR
import jatx.russianrocksongbook.networking.api.result.STATUS_SUCCESS
import jatx.russianrocksongbook.domain.CloudSong
import jatx.russianrocksongbook.networking.api.OrderBy
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.ViewModelDeps
import jatx.russianrocksongbook.viewmodel.interfaces.Cloud
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class CloudViewModel @Inject constructor(
    viewModelDeps: ViewModelDeps,
    val snapshotHolder: SnapshotHolder,
    private val cloudStateHolder: CloudStateHolder
): MvvmViewModel(
    viewModelDeps,
    cloudStateHolder.commonStateHolder
), Cloud {

    private val cloudSongCount = cloudStateHolder.cloudSongCount.asStateFlow()
    private val cloudSong = cloudStateHolder.cloudSong.asStateFlow()

    val isCloudLoading = cloudStateHolder.isCloudLoading.asStateFlow()
    val isListEmpty = cloudStateHolder.isListEmpty.asStateFlow()
    val cloudSongPosition = cloudStateHolder.cloudSongPosition.asStateFlow()

    val scrollPosition = cloudStateHolder.scrollPosition.asStateFlow()
    var isLastOrientationPortrait = cloudStateHolder.isLastOrientationPortrait.asStateFlow()
    val wasOrientationChanged = cloudStateHolder.wasOrientationChanged.asStateFlow()
    val needScroll = cloudStateHolder.needScroll.asStateFlow()

    val cloudSongsFlow = cloudStateHolder.cloudSongsFlow.asStateFlow()

    val wasFetchDataError = cloudStateHolder.wasFetchDataError.asStateFlow()

    val searchFor = cloudStateHolder.searchFor.asStateFlow()
    val orderBy = cloudStateHolder.orderBy.asStateFlow()

    val invalidateCounter = cloudStateHolder.invalidateCounter.asStateFlow()

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        updateFetchDataError(false)
        updateLoading(true)
        updateListIsEmpty(false)
        updateScrollPosition(0)
        updateNeedScroll(true)
        updateSearchFor(searchFor)
        updateOrderBy(orderBy)
        snapshotHolder.isFlowInitDone = false
        cloudStateHolder.cloudSongsFlow.value =
            Pager(CONFIG) {
                CloudSongSource(songBookAPIAdapter, searchFor, orderBy) {
                    updateFetchDataError(true)
                }
            }.flow.onEach {
                if (!snapshotHolder.isFlowInitDone) {
                    snapshotHolder.snapshot = null
                    snapshotHolder.isFlowInitDone = true
                }
            }
    }

    private fun updateFetchDataError(value: Boolean) {
        cloudStateHolder.wasFetchDataError.value = value
    }

    fun updateLoading(value: Boolean) {
        cloudStateHolder.isCloudLoading.value = value
    }

    fun updateListIsEmpty(value: Boolean) {
        cloudStateHolder.isListEmpty.value = value
    }

    fun updateScrollPosition(position: Int) {
        cloudStateHolder.scrollPosition.value = position
    }

    fun updateOrientationWasChanged(value: Boolean) {
        cloudStateHolder.wasOrientationChanged.value = value
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
        cloudSong.value?.apply {
            songBookAPIAdapter
                .delete(secret1, secret2, this)
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
        cloudSong.value?.apply {
            songRepo.addSongFromCloud(this)
            showToast(R.string.toast_chords_saved_and_added_to_favorite)
        }
    }

    @SuppressLint("CheckResult")
    fun voteForCurrent(voteValue: Int) {
        cloudSong.value?.apply {
            songBookAPIAdapter
                .vote(this, userInfo, voteValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            if (voteValue == 1) {
                                this@apply.likeCount += 1
                            } else if (voteValue == -1) {
                                this@apply.dislikeCount += 1
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

    override fun openYandexMusicCloud(dontAskMore: Boolean) {
        settings.yandexMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            callbacks.onOpenYandexMusic("$artist $title")
        }
    }

    override fun openVkMusicCloud(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            callbacks.onOpenVkMusic("$artist $title")
        }
    }

    override fun openYoutubeMusicCloud(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            callbacks.onOpenYoutubeMusic("$artist $title")
        }
    }

    @SuppressLint("CheckResult")
    override fun sendWarningCloud(comment: String) {
        cloudSong.value?.apply {
            songBookAPIAdapter
                .addWarning(this, comment)
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