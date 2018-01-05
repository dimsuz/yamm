package com.dimsuz.yamm.domain.util

interface AppConfig {
  fun setServerUrl(serverUrl: String)
  fun getServerUrl(): String?
}

