package jatx.russianrocksongbook.domain.usecase

import jatx.russianrocksongbook.domain.repository.CloudRepository
import jatx.russianrocksongbook.domain.repository.OrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PagedSearchUseCase @Inject constructor(
    private val cloudRepository: CloudRepository
) {
    fun execute(searchFor: String, orderBy: OrderBy, page: Int) = cloudRepository
        .pagedSearch(searchFor, orderBy, page)
}