package com.dimsuz.yamm.domain.util

interface AppConfig {
  fun setServerUrl(serverUrl: String)
  fun getServerUrl(): String?
  fun getLastUsedChannelId(userId: String, teamId: String): String?
  fun setLastUsedChannelId(userId: String, teamId: String, channelId: String)
}

