package com.dimsuz.yamm.repositories.di

import com.dimsuz.yamm.data.sources.di.bindDataSourcesCommonDependencies
import com.dimsuz.yamm.data.sources.di.bindDataSourcesDependencies
import com.dimsuz.yamm.data.sources.network.models.AuthSession
import com.dimsuz.yamm.domain.repositories.ServerConfigRepository
import com.dimsuz.yamm.repositories.ServerConfigRepositoryImpl
import com.dimsuz.yamm.repositories.session.DefaultSessionManager
import com.dimsuz.yamm.repositories.session.SessionManager
import com.dimsuz.yamm.repositories.settings.PreferencesSettingsStorage
import com.dimsuz.yamm.repositories.settings.SettingsStorage
import toothpick.config.Module
import javax.inject.Inject
import javax.inject.Provider

class RepositoriesModule(serverUrl: String) : Module() {
  init {
    bindDataSourcesDependencies(this, serverUrl)
    bind(ServerConfigRepository::class.java).to(ServerConfigRepositoryImpl::class.java)
  }
}

class RepositoriesCommonModule : Module() {
  init {
    bindDataSourcesCommonDependencies(this)
    bind(SessionManager::class.java).to(DefaultSessionManager::class.java).singletonInScope()
    bind(SettingsStorage::class.java).to(PreferencesSettingsStorage::class.java).singletonInScope()
    bind(AuthSession::class.java).toProvider(AuthSessionProvider::class.java).singletonInScope()
  }
}

internal class AuthSessionProvider @Inject constructor(private val sessionManager: SessionManager)
  : Provider<AuthSession?> {
  override fun get(): AuthSession? {
    return sessionManager.currentSessionToken?.let { AuthSession(it) }
  }
}