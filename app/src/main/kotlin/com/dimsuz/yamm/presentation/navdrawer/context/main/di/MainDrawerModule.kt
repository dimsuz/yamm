package com.dimsuz.yamm.presentation.navdrawer.context.main.di

import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import toothpick.config.Module

class MainDrawerModule : Module() {
  init {
    bind(UserChannelsInteractor::class.java)
  }
}