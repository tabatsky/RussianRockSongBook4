package jatx.russianrocksongbook.commonappstate

import jatx.russianrocksongbook.domain.repository.preferences.SettingsRepository
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Единственный holder состояния приложения.
 * Хранит AppState, внутри которого лежат все остальные UIState.
 */
@Singleton
class AppStateHolder @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    private val _appStateFlow = MutableStateFlow(AppState.initial(settingsRepository.defaultArtist))

    val appStateFlow = _appStateFlow.asStateFlow()

    fun changeAppState(appState: AppState) {
        _appStateFlow.value = appState
    }

    /** Атомарное изменение AppState (не затирает UIState, изменённые параллельно). */
    fun updateAppState(transform: (AppState) -> AppState) {
        _appStateFlow.update(transform)
    }

    /** Сбрасывает собственные поля AppState; вложенные UIState сбрасываются через resetState(key). */
    fun reset() {
        _appStateFlow.update {
            AppState.initial(settingsRepository.defaultArtist).copy(uiStates = it.uiStates)
        }
    }

    fun <T : UIState> stateFlow(key: UIStateKey<T>): StateFlow<T> =
        DerivedStateFlow(_appStateFlow) { it.uiState(key) }

    fun <T : UIState> getState(key: UIStateKey<T>): T =
        _appStateFlow.value.uiState(key)

    fun <T : UIState> changeState(key: UIStateKey<T>, state: T) {
        _appStateFlow.update { it.withUiState(key.name, state) }
    }

    fun <T : UIState> updateState(key: UIStateKey<T>, transform: (T) -> T) {
        _appStateFlow.update { it.withUiState(key.name, transform(it.uiState(key))) }
    }

    fun <T : UIState> resetState(key: UIStateKey<T>) {
        changeState(key, key.initial())
    }
}

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class DerivedStateFlow<S, T>(
    private val source: StateFlow<S>,
    private val transform: (S) -> T
) : StateFlow<T> {

    override val value: T
        get() = transform(source.value)

    override val replayCache: List<T>
        get() = listOf(value)

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        source.map { transform(it) }.distinctUntilChanged().collect(collector)
        throw IllegalStateException("StateFlow collection never completes.")
    }
}
