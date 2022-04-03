package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetArtistsUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute() = localRepository.getArtists()
}