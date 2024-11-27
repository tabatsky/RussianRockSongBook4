package jatx.russianrocksongbook.cloudsongs.internal.viewmodel

enum class SearchState {
    LOADING_FIRST_PAGE,
    LOADING_NEXT_PAGE,
    PAGE_LOADING_SUCCESS,
    NO_MORE_PAGES,
    EMPTY,
    ERROR
}