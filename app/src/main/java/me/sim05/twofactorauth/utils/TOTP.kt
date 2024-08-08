package me.sim05.twofactorauth.utils

import java.util.Formatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

fun totp(key: String): String {
    val time = (System.currentTimeMillis() / 1000).floorDiv(30)
    return hotp(time.toString(), key)
}

fun hotp(data: String, key: String): String {
        val macResult = Hmac.digest(data, key)
        val code = truncate(macResult.toByteArray(Charsets.UTF_8)).rem(
            10.toDouble().pow(6.toDouble())
        ).toInt()
        return code.toString().padStart(6, '0')
}

fun truncate(byteArray: ByteArray): Int {
    val offset = (byteArray[byteArray.size - 1].toInt() and 0xf)
    return (byteArray[offset].toInt() and 0x7f shl 24
            or (byteArray[offset + 1].toInt() and 0xff shl 16)
            or (byteArray[offset + 2].toInt() and 0xff shl 8)
            or (byteArray[offset + 3].toInt() and 0xff))
}

object Hmac {
    fun digest(
        msg: String,
        key: String,
        alg: String = "HmacSHA1"
    ): String {
        val signingKey = SecretKeySpec(key.toByteArray(), alg)
        val mac = Mac.getInstance(alg)
        mac.init(signingKey)

        val bytes = mac.doFinal(msg.toByteArray())
        return format(bytes)
    }

    private fun format(bytes: ByteArray): String {
        val formatter = Formatter()
        bytes.forEach { formatter.format("%02x", it) }
        return formatter.toString()
    }
}
