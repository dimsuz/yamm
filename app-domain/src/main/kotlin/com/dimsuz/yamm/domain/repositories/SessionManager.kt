package com.dimsuz.yamm.domain.repositories

interface SessionManager {
  val currentUserId: String?
  val currentSessionToken: String?

  fun onNewSessionCreated(token: String, userId: String)
}