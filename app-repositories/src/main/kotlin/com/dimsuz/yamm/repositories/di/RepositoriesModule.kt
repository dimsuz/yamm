package com.dimsuz.yamm.repositories.di

import com.dimsuz.yamm.repositories.ServerConfigRepository
import toothpick.config.Module

class RepositoriesModule : Module() {
  init {
    bind(ServerConfigRepository::class.java)
  }
}