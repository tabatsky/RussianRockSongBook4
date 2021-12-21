package jatx.russianrocksongbook.debug.debug

import java.io.PrintWriter
import java.io.StringWriter

fun exceptionToString(e: Throwable): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    e.printStackTrace(pw)
    return sw.toString()
}