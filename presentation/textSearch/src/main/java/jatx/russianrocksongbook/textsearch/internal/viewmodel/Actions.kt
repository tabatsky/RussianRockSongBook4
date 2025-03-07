package jatx.russianrocksongbook.textsearch.internal.viewmodel

import jatx.russianrocksongbook.commonviewmodel.UIAction
import jatx.russianrocksongbook.domain.repository.local.TextSearchOrderBy

data class PerformTextSearch(val searchFor: String, val orderBy: TextSearchOrderBy): UIAction
data class UpdateSearchFor(val searchFor: String): UIAction
data class UpdateOrderBy(val orderBy: TextSearchOrderBy): UIAction
data class UpdateCurrentSongCount(val count: Int): UIAction