package com.finsera.data.source.local

import com.finsera.common.utils.Constant.Companion.USER_ACCESS_TOKEN_KEY
import com.finsera.common.utils.Constant.Companion.USER_LOGGED_IN_STATUS
import com.finsera.common.utils.Constant.Companion.USER_REFRESH_TOKEN_KEY
import com.finsera.common.utils.sharedpref.SharedPreferenceManager

class LocalDataSource(
    private val sharedPreferencesManager: SharedPreferenceManager
) {
    fun setAccessToken(token: String) {
        sharedPreferencesManager.saveString(USER_ACCESS_TOKEN_KEY, token)
    }

    fun getAccessToken() : String {
        return sharedPreferencesManager.getString(USER_ACCESS_TOKEN_KEY, "")
    }

    fun setRefreshToken(token: String) {
        sharedPreferencesManager.saveString(USER_REFRESH_TOKEN_KEY, token)
    }

    fun getRefreshToken() : String {
        return sharedPreferencesManager.getString(USER_REFRESH_TOKEN_KEY, "")
    }

    fun setLoginStatus(status: Boolean) {
        sharedPreferencesManager.saveBoolean(USER_LOGGED_IN_STATUS, status)
    }

    fun getLoginStatus() : Boolean {
        return sharedPreferencesManager.getBoolean(USER_LOGGED_IN_STATUS, false)
    }
}