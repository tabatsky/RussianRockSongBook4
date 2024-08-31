package jatx.russianrocksongbook.domain.usecase.local

import jatx.russianrocksongbook.domain.repository.local.LocalRepository
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSongsByTextSearchUseCase @Inject constructor(
    private val localRepository: LocalRepository
) {
    fun execute(words: List<String>, orderBy: TextSearchOrderBy) = localRepository
        .getSongsByTextSearch(words, orderBy)
}