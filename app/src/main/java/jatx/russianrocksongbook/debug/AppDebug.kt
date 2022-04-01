package jatx.russianrocksongbook.debug

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jatx.russianrocksongbook.domain.models.appcrash.AppCrash
import jatx.russianrocksongbook.domain.usecase.SendCrashUseCase
import jatx.russianrocksongbook.domain.models.appcrash.Version
import jatx.russianrocksongbook.util.debug.exceptionToString

object AppDebug {
    fun setAppCrashHandler(
        sendCrashUseCase: SendCrashUseCase,
        version: Version
    ) {

        val oldHandler =
            Thread.getDefaultUncaughtExceptionHandler()
        if (oldHandler is AppCrashHandler) {
            Log.e("crash handler", "already set")
            return
        }
        Thread
            .setDefaultUncaughtExceptionHandler(
                AppCrashHandler(oldHandler, sendCrashUseCase, version))
    }
}

class AppCrashHandler(
    private val oldHandler: Thread.UncaughtExceptionHandler?,
    private val sendCrashUseCase: SendCrashUseCase,
    private val version: Version
    ) : Thread.UncaughtExceptionHandler {

    @SuppressLint("CheckResult")
    override fun uncaughtException(
        thread: Thread,
        throwable: Throwable
    ) {
        Log.e("app crash", exceptionToString(throwable))
        sendCrashUseCase
            .execute(AppCrash(version, throwable))
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
