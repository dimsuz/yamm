package com.dimsuz.yamm.data.sources.network.session

interface SessionManager {
  fun initializeSession()
  fun onNewSessionCreated(token: String, userId: String)

  val currentUserId: String?
}