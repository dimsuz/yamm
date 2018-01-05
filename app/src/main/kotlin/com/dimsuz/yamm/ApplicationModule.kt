package com.dimsuz.yamm

import android.content.Context
import com.dimsuz.yamm.core.annotations.ApplicationContext
import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.domain.util.AppConfig
import com.dimsuz.yamm.presentation.navdrawer.context.base.DefaultNavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.presentation.navdrawer.context.main.MainNonAuthorizedNavDrawerContextFactory
import com.dimsuz.yamm.domain.util.AppSchedulers
import com.dimsuz.yamm.util.DefaultAppSchedulers
import com.dimsuz.yamm.util.DefaultErrorDetailsExtractor
import com.dimsuz.yamm.util.ErrorDetailsExtractor
import com.dimsuz.yamm.util.PrefsBasedAppConfig
import com.dimsuz.yamm.util.TimberLogger
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
    bind(Logger::class.java).toInstance(TimberLogger())

    bind(Router::class.java).toInstance(cicerone.router)
    bind(NavigatorHolder::class.java).toProviderInstance({ cicerone.navigatorHolder })

    bind(NavDrawerContextFactory::class.java).toInstance(MainNonAuthorizedNavDrawerContextFactory())
    bind(NavDrawerContextManager::class.java).to(DefaultNavDrawerContextManager::class.java)
      .singletonInScope()
  }
}