package jatx.russianrocksongbook.viewmodel

data class ValueWrapper<T>(
    val value: T,
    val counter: Int = 0
) {
    fun withNewValue(value: T) = copy(value = value, counter = counter + 1)
}