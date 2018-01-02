package com.dimsuz.yamm.presentation.messages

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.BindView
import com.dimsuz.yamm.presentation.baseui.Resettable
import com.dimsuz.yamm.presentation.baseui.ScopedMviController
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance
import io.reactivex.subjects.BehaviorSubject
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
  private var foregroundStateChanges: BehaviorSubject<Boolean> = BehaviorSubject.create()

  override fun createPresenter(): MessagesPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
    messagesAdapter = MessagesAdapter()
    messageListView.layoutManager = LinearLayoutManager(activityUnsafe, LinearLayoutManager.VERTICAL, true)
    messageListView.adapter = messagesAdapter
    val postsInteractor = screenScope.instance<ChannelPostsInteractor>()
    postsInteractor.setForegroundStateChanges(foregroundStateChanges)
  }

  override fun onAttach(view: View) {
    super.onAttach(view)
    // it is too early to setup nav drawer in initializeView - it may not be created by that time
    val navDrawerView = (activityUnsafe as MainActivity).navDrawerView
    navDrawerView.configureToolbar(toolbar)
  }

  override fun onActivityStarted(activity: Activity) {
    super.onActivityStarted(activity)
    foregroundStateChanges.onNext(true)
  }

  override fun onActivityStopped(activity: Activity) {
    super.onActivityStopped(activity)
    foregroundStateChanges.onNext(false)
  }

  override fun onDestroy() {
    val postsInteractor = screenScope.instance<ChannelPostsInteractor>()
    postsInteractor.resetForegroundStateChanges()
    // onActivityStopped might not be always called
    // (for example when pressing back it isn't - due to Conductor issues)
    // for those cases report it from here too
    foregroundStateChanges.onNext(false)
    super.onDestroy()
  }

  override fun renderViewState(viewState: Messages.ViewState) {
    if (previousViewState?.posts != viewState.posts) {
      messagesAdapter.setData(viewState.posts)
    }
  }

}
