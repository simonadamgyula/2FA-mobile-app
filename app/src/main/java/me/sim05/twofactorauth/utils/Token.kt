package me.sim05.twofactorauth.utils

class Token(private val secret: String) {
    fun getToken(): String {
        return (System.currentTimeMillis() % 1000000L).toString()
    }
}