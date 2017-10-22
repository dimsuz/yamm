package com.dimsuz.yamm.settings

interface SettingsStorage {
  fun saveValue(key: String, value: String)
  fun getValue(key: String): String?
}