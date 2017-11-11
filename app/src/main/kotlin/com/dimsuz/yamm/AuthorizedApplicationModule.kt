package com.dimsuz.yamm

import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import com.dimsuz.yamm.presentation.navdrawer.context.base.DefaultNavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.main.MainNavDrawerContextFactory
import toothpick.config.Module

class AuthorizedApplicationModule : Module() {
  init {
    bind(UserChannelsInteractor::class.java).to(UserChannelsInteractor::class.java).singletonInScope()
    bind(NavDrawerContextFactory::class.java).to(MainNavDrawerContextFactory::class.java).singletonInScope()
    bind(NavDrawerContextManager::class.java).to(DefaultNavDrawerContextManager::class.java).singletonInScope()
  }
}