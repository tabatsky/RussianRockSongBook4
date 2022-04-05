package jatx.russianrocksongbook.util.hashing

import android.util.Log
import org.apache.commons.lang3.RandomStringUtils
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HashingUtil {
    private val digest = try {
        MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
        Log.e("hash", "no MD5 digest found")
        null
    }

    fun hash(text: String): String = digest?.let {
        it.reset()
        it.update(text.toByteArray())
        BigInteger(1, it.digest()).toString(16)
    } ?: run {
        Log.e("hash", "using random")
        RandomStringUtils.randomAlphanumeric(20)
    }
}


