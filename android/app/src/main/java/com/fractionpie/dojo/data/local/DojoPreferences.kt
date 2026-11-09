package com.fractionpie.dojo.data.local

import android.content.Context
import android.content.SharedPreferences

class DojoPreferences(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(STORE_NAME, Context.MODE_PRIVATE)

    fun readInt(key: String, fallback: Int): Int = preferences.getInt(key, fallback)

    fun writeInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun readBoolean(key: String, fallback: Boolean): Boolean =
        preferences.getBoolean(key, fallback)

    fun writeBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun readString(key: String, fallback: String): String =
        preferences.getString(key, fallback) ?: fallback

    fun writeString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun clear() {
        preferences.edit().clear().apply()
    }

    private companion object {
        const val STORE_NAME = "fraction_pie_dojo"
    }
}
