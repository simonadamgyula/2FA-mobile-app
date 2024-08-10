package me.sim05.twofactorauth

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)

    fun <T> saveData(key: String, value: T) {
        val editor = sharedPreferences.edit()
        when (value) {
            is String -> editor.putString(key, value as String)
            is Int -> editor.putInt(key, value as Int)
            is Boolean -> editor.putBoolean(key, value as Boolean)
            is Long -> editor.putLong(key, value as Long)
            is Float -> editor.putFloat(key, value as Float)
            else -> throw IllegalArgumentException("Type not supported")
        }
        editor.apply()
    }

    fun getData(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun toggleBoolean(key: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, !sharedPreferences.getBoolean(key, false))
        editor.apply()
    }
}