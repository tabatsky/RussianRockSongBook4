package jatx.russianrocksongbook.localsongs.internal.viewmodel

sealed class DrawerState

object DrawerStateOpened: DrawerState()
object DrawerStateClosed: DrawerState()