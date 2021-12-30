package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.models.AppCrash
import jatx.russianrocksongbook.domain.repository.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SendCrashUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(appCrash: AppCrash) = cloudRepository
        .sendCrash(appCrash)
}