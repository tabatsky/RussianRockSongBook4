package jatx.russianrocksongbook.domain.usecase.cloud

import jatx.russianrocksongbook.domain.repository.cloud.CloudRepository
import jatx.russianrocksongbook.domain.repository.cloud.CloudSearchOrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagedSearchUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    suspend fun execute(searchFor: String, orderBy: CloudSearchOrderBy, page: Int) = cloudRepository
        .pagedSearch(searchFor, orderBy, page)
}