package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.AppCrash
import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.repository.OrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendCrashUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository
) {
    fun execute(appCrash: AppCrash) = cloudSongRepository
        .sendCrash(appCrash)
}