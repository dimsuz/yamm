package com.dimsuz.yamm.repositories.settings

import android.content.Context
import android.preference.PreferenceManager
import com.dimsuz.yamm.core.annotations.ApplicationContext
import javax.inject.Inject

internal class PreferencesSettingsStorage @Inject constructor(@ApplicationContext private val context: Context) : SettingsStorage {

  override fun saveValue(key: String, value: String) {
    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(key, value).apply()
  }

  override fun getValue(key: String): String? {
    return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null)
  }

}