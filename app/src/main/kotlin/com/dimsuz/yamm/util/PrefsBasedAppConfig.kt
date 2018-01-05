package com.dimsuz.yamm.util

import android.content.Context
import android.preference.PreferenceManager
import com.dimsuz.yamm.BuildConfig
import com.dimsuz.yamm.core.annotations.ApplicationContext
import com.dimsuz.yamm.domain.util.AppConfig
import javax.inject.Inject

private const val PREF_KEY_SERVER_URL = BuildConfig.APPLICATION_ID + ".server_url"
private const val PREF_KEY_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel_id"

class PrefsBasedAppConfig @Inject constructor(@ApplicationContext private val context: Context) : AppConfig {

  override fun setServerUrl(serverUrl: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putString(PREF_KEY_SERVER_URL, serverUrl).apply()
  }

  override fun getServerUrl(): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString(PREF_KEY_SERVER_URL, null)
  }

  override fun getLastUsedChannelId(userId: String, teamId: String): String? {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    return prefs.getString(userTeamKey(userId, teamId, PREF_KEY_CHANNEL_ID), null)
  }

  override fun setLastUsedChannelId(userId: String, teamId: String, channelId: String) {
    val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    prefs.edit().putString(userTeamKey(userId, teamId, PREF_KEY_CHANNEL_ID), channelId).apply()
  }

  private fun userTeamKey(userId: String, teamId: String, key: String) = "${userId}_${teamId}_$key"

}