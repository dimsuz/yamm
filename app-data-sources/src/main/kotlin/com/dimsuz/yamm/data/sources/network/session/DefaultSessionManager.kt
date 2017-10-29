package com.dimsuz.yamm.data.sources.network.session

import com.dimsuz.yamm.data.sources.network.services.MattermostService
import com.dimsuz.yamm.data.sources.network.services.SessionExpirationGuardProvider
import com.dimsuz.yamm.data.sources.settings.SettingsStorage
import io.reactivex.SingleTransformer
import timber.log.Timber
import javax.inject.Inject

private const val SETTINGS_KEY_AUTH_TOKEN = "session_token"
private const val SETTINGS_KEY_AUTH_USER_ID = "session_user_id"

internal class DefaultSessionManager @Inject constructor(private val settings: SettingsStorage,
                                                         private val mattermostService: MattermostService) : SessionManager {

  init {
    // TODO remove
    Timber.e("CREATING Session manager")
  }

  override val currentUserId: String?
    get() = settings.getValue(SETTINGS_KEY_AUTH_USER_ID)

  override fun initializeSession() {
    if (currentUserId == null) {
      Timber.d("session token is not available, authorized requests won't work until logged in")
    }
    mattermostService.authToken = currentUserId
    mattermostService.sessionExpirationGuard = createSessionExpirationGuard()
  }

  override fun onNewSessionCreated(token: String, userId: String) {
    Timber.d("creating a new session: token=$token, userId=$userId")
    settings.saveValue(SETTINGS_KEY_AUTH_TOKEN, token)
    settings.saveValue(SETTINGS_KEY_AUTH_USER_ID, userId)

    mattermostService.authToken = token
  }

  private fun createSessionExpirationGuard(): SessionExpirationGuardProvider {
    return object : SessionExpirationGuardProvider {
      override fun <T> get(): SingleTransformer<T, T> {
        // TODO guard and clear session!!!
        return SingleTransformer { s -> s }
      }
    }
  }
}