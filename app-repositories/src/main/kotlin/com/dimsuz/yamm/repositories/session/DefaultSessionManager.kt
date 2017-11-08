package com.dimsuz.yamm.repositories.session

import com.dimsuz.yamm.repositories.settings.SettingsStorage
import timber.log.Timber
import javax.inject.Inject

private const val SETTINGS_KEY_AUTH_TOKEN = "session_token"
private const val SETTINGS_KEY_AUTH_USER_ID = "session_user_id"

internal class DefaultSessionManager @Inject constructor(private val settings: SettingsStorage) : SessionManager {

  init {
    // TODO remove
    Timber.e("CREATING Session manager")
  }

  override val currentUserId: String?
    get() = settings.getValue(SETTINGS_KEY_AUTH_USER_ID)

  override val currentSessionToken: String?
    get() = settings.getValue(SETTINGS_KEY_AUTH_TOKEN)

  override fun onNewSessionCreated(token: String, userId: String) {
    Timber.d("creating a new session: token=$token, userId=$userId")
    settings.saveValue(SETTINGS_KEY_AUTH_TOKEN, token)
    settings.saveValue(SETTINGS_KEY_AUTH_USER_ID, userId)
  }
}