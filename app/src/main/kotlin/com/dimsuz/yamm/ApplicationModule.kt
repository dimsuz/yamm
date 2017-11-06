package com.dimsuz.yamm

import android.content.Context
import com.dimsuz.yamm.domain.di.ApplicationContext
import com.dimsuz.yamm.presentation.navdrawer.context.base.DefaultNavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.main.MainNavDrawerContextFactory
import com.dimsuz.yamm.util.AppConfig
import com.dimsuz.yamm.util.AppSchedulers
import com.dimsuz.yamm.util.DefaultAppSchedulers
import com.dimsuz.yamm.util.DefaultErrorDetailsExtractor
import com.dimsuz.yamm.util.ErrorDetailsExtractor
import com.dimsuz.yamm.util.PrefsBasedAppConfig
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module

internal class ApplicationModule(application: YammApplication, cicerone: Cicerone<Router>) : Module() {
  init {
    bind(Context::class.java).withName(ApplicationContext::class.java).toInstance(application)
    bind(YammApplication::class.java).toInstance(application)
    bind(AppSchedulers::class.java).toInstance(DefaultAppSchedulers)
    bind(ErrorDetailsExtractor::class.java).toInstance(DefaultErrorDetailsExtractor())
    // not making it singleton, will be recreated each time it's needed.
    // Currently I think it will be rarely needed, but if this will become wrong, rethink
    bind(AppConfig::class.java).to(PrefsBasedAppConfig::class.java)

    bind(Router::class.java).toInstance(cicerone.router)
    bind(NavigatorHolder::class.java).toProviderInstance({ cicerone.navigatorHolder })

    bind(NavDrawerContextFactory::class.java).to(MainNavDrawerContextFactory::class.java).singletonInScope()
    bind(NavDrawerContextManager::class.java).to(DefaultNavDrawerContextManager::class.java).singletonInScope()
  }
}