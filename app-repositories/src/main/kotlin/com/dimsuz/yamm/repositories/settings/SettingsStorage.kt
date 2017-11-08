package com.dimsuz.yamm.repositories.settings

interface SettingsStorage {
  fun saveValue(key: String, value: String)
  fun getValue(key: String): String?
}