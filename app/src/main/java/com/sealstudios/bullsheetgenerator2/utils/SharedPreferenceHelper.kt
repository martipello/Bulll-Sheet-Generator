package com.sealstudios.bullsheetgenerator2.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(prefsFileName, 0)

    fun getBool(key: String, default: Boolean = true): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun setBool(key: String, value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    companion object {
        const val prefsFileName = "com.sealstudios.bullsheetgenerator2.prefs"
        const val isFirstTime = "firstStarted"
    }

}