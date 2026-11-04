package jatx.russianrocksongbook.commonappstate

data class AppState(
    val currentArtist: String,
    val appWasUpdated: Boolean,
    val artistList: List<String>,
    val lastRandomKey: Int,
    /**
     * Все прочие state приложения (LocalState, CloudState, ...) в виде UIState.
     * Приведение к конкретному типу делается по UIStateKey, объявленному в модуле-владельце state.
     */
    val uiStates: Map<String, UIState> = emptyMap()
) : UIState {

    fun <T : UIState> uiState(key: UIStateKey<T>): T =
        key.clazz.cast(uiStates[key.name]) ?: key.initial()

    fun withUiState(name: String, state: UIState): AppState =
        copy(uiStates = uiStates + (name to state))

    fun withoutUiState(name: String): AppState =
        copy(uiStates = uiStates - name)

    companion object {
        fun initial(defaultArtist: String) = AppState(
            currentArtist = defaultArtist,
            appWasUpdated = false,
            artistList = listOf(),
            lastRandomKey = 0
        )
    }
}
