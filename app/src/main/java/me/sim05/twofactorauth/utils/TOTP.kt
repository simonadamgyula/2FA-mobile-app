package me.sim05.twofactorauth.utils

import org.apache.commons.codec.binary.Base32
import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

const val codeLength = 6

fun formattedTotp(secret: ByteArray): String {
    val totp = generateTotp(secret)
    return totp.chunked(3).joinToString(" ")
}

fun generateTotp(secret: ByteArray): String {
    val counter = System.currentTimeMillis().milliseconds.inWholeSeconds.floorDiv(30)
    val payload: ByteArray = ByteBuffer.allocate(8).putLong(0, counter).array()
    val hash = generateHash(secret, payload)
    val truncatedHash = truncateHash(hash)
    val code = ByteBuffer.wrap(truncatedHash).int % 10.0.pow(codeLength).toInt()
    return code.toString().padStart(codeLength, '0')
}

private fun generateHash(secret: ByteArray, payload: ByteArray): ByteArray {
    val key = Base32().decode(secret)
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(SecretKeySpec(key, "RAW"))
    return mac.doFinal(payload)
}

private fun truncateHash(hash: ByteArray): ByteArray {
    val offset = hash.last().and(0x0F).toInt()
    val truncatedHash = ByteArray(4)
    for (i in 0..3) {
        truncatedHash[i] = hash[offset + i]
    }
    truncatedHash[0] = truncatedHash[0].and(0x7F)
    return truncatedHash
}