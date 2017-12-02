package com.dimsuz.yamm.presentation.messages

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.BindView
import com.dimsuz.yamm.presentation.baseui.Resettable
import com.dimsuz.yamm.presentation.baseui.ScopedMviController
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance
import timber.log.Timber
import toothpick.config.Module

class MessagesController : ScopedMviController<Messages.ViewState, Messages.View, MessagesPresenter>() {

  override fun createScopedConfig() = object : Config {
    override val viewLayoutResource = R.layout.messages
    override val screenModule = object : Module() {
      init {
        bind(MessagesPresenter::class.java).to(MessagesPresenter::class.java)
      }
    }
  }

  private val toolbar: Toolbar by BindView(R.id.toolbar)
  private val messageListView: RecyclerView by BindView(R.id.message_list)
  private var messagesAdapter: MessagesAdapter by Resettable()

  override fun createPresenter(): MessagesPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
    messagesAdapter = MessagesAdapter()
    messageListView.layoutManager = LinearLayoutManager(activityUnsafe, LinearLayoutManager.VERTICAL, true)
    messageListView.adapter = messagesAdapter
  }

  override fun onAttach(view: View) {
    super.onAttach(view)
    // it is too early to setup nav drawer in initializeView - it may not be created by that time
    val navDrawerView = (activityUnsafe as MainActivity).navDrawerView
    navDrawerView.configureToolbar(toolbar)
  }

  override fun renderViewState(viewState: Messages.ViewState) {
    if (previousViewState?.posts != viewState.posts) {
      Timber.d(viewState.posts.firstOrNull().toString())
      messagesAdapter.setData(viewState.posts)
    }
  }

}
