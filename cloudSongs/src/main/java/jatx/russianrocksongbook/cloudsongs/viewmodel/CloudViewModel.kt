package jatx.russianrocksongbook.cloudsongs.viewmodel

import android.annotation.SuppressLint
import androidx.paging.Pager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.cloudsongs.paging.CONFIG
import jatx.russianrocksongbook.cloudsongs.paging.CloudSongSource
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.domain.formatRating
import jatx.russianrocksongbook.viewmodel.MvvmViewModel
import jatx.russianrocksongbook.viewmodel.R
import jatx.russianrocksongbook.viewmodel.ViewModelParam
import jatx.russianrocksongbook.viewmodel.interfaces.Cloud
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CloudViewModel @Inject constructor(
    viewModelParam: ViewModelParam,
    private val cloudScreenStateHolder: CloudScreenStateHolder
): MvvmViewModel(
    viewModelParam,
    cloudScreenStateHolder.screenStateHolder
), Cloud {

    val isCloudLoading = cloudScreenStateHolder.isCloudLoading.asStateFlow()
    val cloudSongCount = cloudScreenStateHolder.cloudSongCount.asStateFlow()
    val cloudSongPosition = cloudScreenStateHolder.cloudSongPosition.asStateFlow()
    val cloudSong = cloudScreenStateHolder.cloudSong.asStateFlow()

    val latestPosition = cloudScreenStateHolder.latestPosition.asStateFlow()
    val listPosition = cloudScreenStateHolder.listPosition.asStateFlow()

    val cloudSongsFlow = cloudScreenStateHolder.cloudSongsFlow.asStateFlow()

    val wasFetchDataError = cloudScreenStateHolder.wasFetchDataError.asStateFlow()

    private var voteDisposable: Disposable? = null
    private var sendWarningDisposable: Disposable? = null

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        setFetchDataError(false)
        updateListPosition(0)
        setLatestPosition(-1)
        cloudScreenStateHolder.cloudSongsFlow.value =
            Pager(CONFIG) {
                CloudSongSource(songBookAPIAdapter, searchFor, orderBy) {
                    setFetchDataError(true)
                }
            }.flow
    }

    private fun setFetchDataError(value: Boolean) {
        cloudScreenStateHolder.wasFetchDataError.value = value
    }

    fun setLoading(value: Boolean) {
        cloudScreenStateHolder.isCloudLoading.value = value
    }

    fun setLatestPosition(position: Int) {
        cloudScreenStateHolder.latestPosition.value = position
    }

    fun selectCloudSong(position: Int) {
        cloudScreenStateHolder.cloudSongPosition.value = position
    }

    fun updateListPosition(position: Int) {
        cloudScreenStateHolder.listPosition.value = position
    }

    fun updateCloudSong(cloudSong: CloudSong?) {
        cloudScreenStateHolder.cloudSong.value = cloudSong
    }

    fun updateCloudSongCount(count: Int) {
        cloudScreenStateHolder.cloudSongCount.value = count
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

    fun voteForCurrent(voteValue: Int) {
        cloudSong.value?.apply {
            voteDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            voteDisposable = songBookAPIAdapter
                .vote(this, userInfo, voteValue)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result.status) {
                        STATUS_SUCCESS -> {
                            val voteWeight = result.data?.toDouble() ?: 0.0
                            val voteWeightStr = formatRating(voteWeight)
                            val toastText = context.getString(
                                R.string.toast_vote_success, voteWeightStr
                            )
                            showToast(toastText)
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

    override fun sendWarningCloud(comment: String) {
        cloudSong.value?.apply {
            sendWarningDisposable?.apply {
                if (!this.isDisposed) this.dispose()
            }
            sendWarningDisposable = songBookAPIAdapter
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