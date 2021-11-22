package jatx.russianrocksongbook.cloudsongs.viewmodel

import android.annotation.SuppressLint
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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
    val cloudSongList = cloudScreenStateHolder.cloudSongList.asStateFlow()
    val cloudSongPosition = cloudScreenStateHolder.cloudSongPosition.asStateFlow()
    val cloudSong = cloudScreenStateHolder.cloudSong.asStateFlow()

    private var cloudSearchDisposable: Disposable? = null
    private var voteDisposable: Disposable? = null
    private var sendWarningDisposable: Disposable? = null

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        cloudScreenStateHolder.isCloudLoading.value = true
        cloudSearchDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        cloudSearchDisposable = songBookAPIAdapter
            .searchSongs(searchFor, orderBy)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                cloudScreenStateHolder.isCloudLoading.value = false
                when (result.status) {
                    STATUS_ERROR -> showToast(result.message ?: "")
                    STATUS_SUCCESS -> {
                        result.data?.apply {
                            cloudScreenStateHolder.cloudSongList.value = this.map { CloudSong(it) }
                            cloudScreenStateHolder.cloudSongCount.value = this.size
                        }
                    }
                }
            }, { error ->
                error.printStackTrace()
                cloudScreenStateHolder.isCloudLoading.value = false
                showToast(R.string.error_in_app)
            })
    }

    fun selectCloudSong(position: Int) {
        cloudScreenStateHolder.cloudSongPosition.value = position
        cloudScreenStateHolder.cloudSong.value = cloudSongList.value.getOrNull(position)
    }

    fun nextCloudSong() {
        if (cloudSongCount.value > 0) {
            selectCloudSong((cloudSongPosition.value + 1) % cloudSongCount.value)
        }
    }

    fun prevCloudSong() {
        if (cloudSongCount.value > 0) {
            if (cloudSongPosition.value > 0) {
                selectCloudSong((cloudSongPosition.value - 1) % cloudSongCount.value)
            } else {
                selectCloudSong(cloudSongCount.value - 1)
            }
        }
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