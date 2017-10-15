package com.dimsuz.yamm.util

import android.content.Context
import android.preference.PreferenceManager
import com.dimsuz.yamm.BuildConfig
import javax.inject.Inject

private const val PREF_KEY_SERVER_URL = BuildConfig.APPLICATION_ID + ".server_url"

interface AppConfig {
  fun setServerUrl(serverUrl: String)
  fun getServerUrl(): String?
}

class PrefsBasedAppConfig @Inject constructor(private val context: Context) : AppConfig {

  override fun setServerUrl(serverUrl: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putString(PREF_KEY_SERVER_URL, serverUrl).apply()
  }

  override fun getServerUrl(): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString(PREF_KEY_SERVER_URL, null)
  }

}