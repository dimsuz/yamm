package com.dimsuz.yamm.presentation.messages.di

import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import toothpick.config.Module

class MessagesScreenModule : Module() {
  init {
    bind(UserChannelsInteractor::class.java)
  }
}