package com.dimsuz.yamm.presentation.navdrawer.context.main

import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.navigation.payloadAsParam
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import javax.inject.Inject

class MainNavDrawerContextFactory @Inject constructor(
  private val userChannelsInteractor: UserChannelsInteractor,
  private val channelPostsInteractor: ChannelPostsInteractor): NavDrawerContextFactory {

  override fun create(type: DrawerContextType): NavDrawerContext {
    return when (type) {
      DrawerContextType.Messages -> createMessagesContext()
      DrawerContextType.Teams -> TODO("not implemented")
    }
  }

  private fun createMessagesContext(): NavDrawerContext {
    return NavDrawerContext(
      type = DrawerContextType.Messages,
      items = userChannelsInteractor.userChannels()
        .map { chs -> chs.mapIndexed { i, ch -> ch.toDrawerItem(i) } },
      selectionObserver = { drawerItem ->
        userChannelsInteractor.setChannelId(payloadAsParam(drawerItem.payload, "channelId"))
      }
    )
  }
}

private fun Channel.toDrawerItem(i: Int): NavDrawerItem {
  return NavDrawerItem(
    id = i.toLong(),
    title = this.displayName ?: "?",
    payload = this.id
  )
}
