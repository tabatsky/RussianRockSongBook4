package jatx.russianrocksongbook.util.hashing

import android.util.Log
import org.apache.commons.lang3.RandomStringUtils
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object HashingUtil {
    private var digest: MessageDigest? = null

    init {
        var tmpDigest: MessageDigest?
        try {
            tmpDigest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            tmpDigest = null
            e.printStackTrace()
        }
        digest = tmpDigest
    }

    fun md5(text: String): String = digest?.let {
        it.reset()
        it.update(text.toByteArray())
        BigInteger(1, it.digest()).toString(16)
    } ?: run {
        Log.e("songTextHash", "using random")
        RandomStringUtils.randomAlphanumeric(20)
    }
}


