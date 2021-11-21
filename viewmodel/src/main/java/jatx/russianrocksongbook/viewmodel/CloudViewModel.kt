package jatx.russianrocksongbook.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.model.api.gson.STATUS_ERROR
import jatx.russianrocksongbook.model.api.gson.STATUS_SUCCESS
import jatx.russianrocksongbook.model.data.FileSystemAdapter
import jatx.russianrocksongbook.model.data.OrderBy
import jatx.russianrocksongbook.model.data.SongBookAPIAdapter
import jatx.russianrocksongbook.model.data.SongRepository
import jatx.russianrocksongbook.model.domain.CloudSong
import jatx.russianrocksongbook.model.domain.formatRating
import jatx.russianrocksongbook.model.preferences.Settings
import jatx.russianrocksongbook.model.preferences.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CloudViewModel @Inject constructor(
    viewModelParam: ViewModelParam
): MvvmViewModel(viewModelParam) {

    private val _isCloudLoading = MutableStateFlow(false)
    val isCloudLoading = _isCloudLoading.asStateFlow()
    private val _cloudSongCount = MutableStateFlow(0)
    val cloudSongCount = _cloudSongCount.asStateFlow()
    private val _cloudSongList = MutableStateFlow(listOf<CloudSong>())
    val cloudSongList = _cloudSongList.asStateFlow()
    private val _cloudSongPosition = MutableStateFlow(0)
    val cloudSongPosition = _cloudSongPosition.asStateFlow()
    private val _cloudSong: MutableStateFlow<CloudSong?> = MutableStateFlow(null)
    val cloudSong = _cloudSong.asStateFlow()

    private var cloudSearchDisposable: Disposable? = null
    private var voteDisposable: Disposable? = null
    private var sendWarningDisposable: Disposable? = null

    fun cloudSearch(searchFor: String, orderBy: OrderBy) {
        _isCloudLoading.value = true
        cloudSearchDisposable?.apply {
            if (!this.isDisposed) this.dispose()
        }
        cloudSearchDisposable = songBookAPIAdapter
            .searchSongs(searchFor, orderBy)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                _isCloudLoading.value = false
                when (result.status) {
                    STATUS_ERROR -> showToast(result.message ?: "")
                    STATUS_SUCCESS -> {
                        result.data?.apply {
                            _cloudSongList.value = this.map { CloudSong(it) }
                            _cloudSongCount.value = this.size
                        }
                    }
                }
            }, { error ->
                error.printStackTrace()
                _isCloudLoading.value = false
                showToast(R.string.error_in_app)
            })
    }

    fun selectCloudSong(position: Int) {
        _cloudSongPosition.value = position
        _cloudSong.value = cloudSongList.value.getOrNull(position)
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

    fun openYandexMusicCloud(dontAskMore: Boolean) {
        settings.yandexMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            actions.onOpenYandexMusic("$artist $title")
        }
    }

    fun openVkMusicCloud(dontAskMore: Boolean) {
        settings.vkMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            actions.onOpenVkMusic("$artist $title")
        }
    }

    fun openYoutubeMusicCloud(dontAskMore: Boolean) {
        settings.youtubeMusicDontAsk = dontAskMore
        cloudSong.value?.apply {
            actions.onOpenYoutubeMusic("$artist $title")
        }
    }

    fun sendWarningCloud(comment: String) {
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