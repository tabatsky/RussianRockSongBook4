package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.models.warning.Warnable
import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddWarningUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(warnable: Warnable, comment: String) = cloudRepository
        .addWarning(warnable.warningWithComment(comment))
}