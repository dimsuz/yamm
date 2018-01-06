package com.dimsuz.yamm.presentation.navdrawer.context.main

import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.interactors.UserChannelsInteractor
import com.dimsuz.yamm.domain.models.Channel
import com.dimsuz.yamm.navigation.payloadAsParam
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextFactory
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerContext
import com.dimsuz.yamm.presentation.navdrawer.models.NavDrawerItem
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
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
      items = getItemStateChanges(),
      selectionObserver = { drawerItem ->
        userChannelsInteractor.setChannelId(payloadAsParam(drawerItem.payload, "channelId"))
      }
    )
  }

  private fun getItemStateChanges(): Observable<NavDrawerContext.ItemsState> {
    // channelItems() includes mapping to drawer items, ensuring that it won't repeat whenever currentChannel changes,
    // only if items themselves are changed
    return Observable
      .combineLatest(channelItems(), userChannelsInteractor.currentChannel(),
        BiFunction { items: List<NavDrawerItem>, cur: Channel -> items to cur })
      .map { (items, cur) ->
        val selected = items.find { (it.payload as String) == cur.id }
        NavDrawerContext.ItemsState(items, selected)
      }
  }

  private fun channelItems(): Observable<List<NavDrawerItem>> {
    return userChannelsInteractor.userChannels()
      .map { chs -> chs.mapIndexed { i, ch -> ch.toDrawerItem(i) } }
      .doOnNext { Timber.e("reindexing channel drawer items; remove me if you don't see many of me on channel switch") }
  }
}

private fun Channel.toDrawerItem(i: Int): NavDrawerItem {
  return NavDrawerItem(
    id = i.toLong(),
    title = this.displayName ?: "?",
    payload = this.id
  )
}
