package jatx.russianrocksongbook.debug

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.data.SongBookAPIAdapter
import jatx.russianrocksongbook.debug.debug.exceptionToString
import jatx.russianrocksongbook.debug.domain.AppCrash

object AppDebug {
    fun setAppCrashHandler(songBookAPIAdapter: SongBookAPIAdapter) {
        val oldHandler =
            Thread.getDefaultUncaughtExceptionHandler()
        if (oldHandler is AppCrashHandler) {
            Log.e("crash handler", "already set")
            return
        }
        Thread
            .setDefaultUncaughtExceptionHandler(
                AppCrashHandler(oldHandler, songBookAPIAdapter))
    }
}

class AppCrashHandler(
    private val oldHandler: Thread.UncaughtExceptionHandler?,
    private val songBookAPIAdapter: SongBookAPIAdapter
    ) : Thread.UncaughtExceptionHandler {

    @SuppressLint("CheckResult")
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable
    ) {
        Log.e("app crash", exceptionToString(throwable))
        songBookAPIAdapter
            .sendCrash(AppCrash(throwable))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.e("status", result.status)
            }, { error ->
                error.printStackTrace()
            })
        Thread.sleep(3000)
        oldHandler?.uncaughtException(thread, throwable)
    }
}
