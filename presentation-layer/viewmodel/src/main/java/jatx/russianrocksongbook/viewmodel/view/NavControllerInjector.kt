package jatx.russianrocksongbook.viewmodel.view

import androidx.navigation.NavHostController

const val destinationStart = "Start"
const val destinationSongList = "SongList"

const val argArtist = "artist"
const val argIsBackFromSong = "isBackFromSong"

fun injectNavController(navController: NavHostController) {
    NavControllerHolder.navController = navController
}

object NavControllerHolder {
    lateinit var navController: NavHostController
}