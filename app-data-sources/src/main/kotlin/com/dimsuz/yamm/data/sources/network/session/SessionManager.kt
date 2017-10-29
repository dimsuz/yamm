package com.dimsuz.yamm.data.sources.network.session

interface SessionManager {
  val currentUserId: String?
  val currentSessionToken: String?

  fun onNewSessionCreated(token: String, userId: String)
}