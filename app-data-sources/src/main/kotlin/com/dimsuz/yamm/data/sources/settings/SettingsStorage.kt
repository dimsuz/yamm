package com.dimsuz.yamm.data.sources.settings

interface SettingsStorage {
  fun saveValue(key: String, value: String)
  fun getValue(key: String): String?
}