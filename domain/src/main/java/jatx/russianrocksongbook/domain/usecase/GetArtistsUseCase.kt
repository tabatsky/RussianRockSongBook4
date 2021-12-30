package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.repository.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtistsUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute() = localRepository.getArtists()
}