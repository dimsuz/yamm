package com.dimsuz.yamm.domain.di

import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractorImpl
import toothpick.config.Module

class DomainModule : Module() {
  init {
    bind(UserChannelsInteractor::class.java).to(UserChannelsInteractorImpl::class.java).singletonInScope()
    bind(ChannelPostsInteractor::class.java).singletonInScope()
  }
}