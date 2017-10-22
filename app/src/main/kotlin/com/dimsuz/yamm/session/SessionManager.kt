package com.dimsuz.yamm.session

interface SessionManager {
  fun initializeSession()
  fun onNewSessionCreated(token: String, userId: String)

  val currentUserId: String?
}