package jatx.russianrocksongbook.commonappstate

/**
 * Ключ, по которому конкретный UIState лежит в AppState.uiStates.
 *
 * Ключ объявляется в модуле, которому принадлежит state, и несёт в себе конкретный тип T:
 * именно через него UIState приводится к своему типу.
 */
class UIStateKey<T : UIState>(
    val clazz: Class<T>,
    val name: String = clazz.name,
    val initial: () -> T
)
