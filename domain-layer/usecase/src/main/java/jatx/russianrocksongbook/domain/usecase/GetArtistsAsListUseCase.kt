package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtistsAsListUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute() = localRepository.getArtistsAsList()
}