package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.repository.CloudSongRepository
import jatx.russianrocksongbook.domain.repository.OrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagedSearchUseCase @Inject constructor(
    private val cloudSongRepository: CloudSongRepository
) {
    fun execute(searchFor: String, orderBy: OrderBy, page: Int) = cloudSongRepository
        .pagedSearch(searchFor, orderBy, page)
}