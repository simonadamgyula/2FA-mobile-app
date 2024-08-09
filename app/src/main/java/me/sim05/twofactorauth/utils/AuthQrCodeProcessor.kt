package me.sim05.twofactorauth.utils

import android.net.Uri

class AuthQrCodeProcessor(rawValue: String) {
    private var uri: Uri = Uri.parse(rawValue)

    fun getIssuer(): String {
        return uri.getQueryParameter("issuer") ?: ""
    }

    fun getSecret(): String {
        return uri.getQueryParameter("secret") ?: ""
    }

    fun getDetails(): String {
        val authority = uri.path ?: ""
        return authority.split(":").last()
    }
}