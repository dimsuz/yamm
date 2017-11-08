package com.dimsuz.yamm.repositories.session

interface SessionManager {
  val currentUserId: String?
  val currentSessionToken: String?

  fun onNewSessionCreated(token: String, userId: String)
}