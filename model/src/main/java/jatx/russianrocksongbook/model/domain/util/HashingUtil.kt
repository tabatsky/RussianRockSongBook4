package jatx.russianrocksongbook.model.domain.util

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

    fun md5(text: String): String {
        return if (digest != null) {
            digest!!.reset()
            digest!!.update(text.toByteArray())
            //Log.e("md5Hash", hash);
            BigInteger(1, digest!!.digest()).toString(16)
        } else {
            val hash: String = RandomStringUtils.randomAlphanumeric(20)
            Log.e("randomHash", hash)
            hash
        }
    }
}
