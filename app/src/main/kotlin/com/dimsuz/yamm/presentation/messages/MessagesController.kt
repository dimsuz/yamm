package com.dimsuz.yamm.presentation.messages

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.dimsuz.yamm.R
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.presentation.MainActivity
import com.dimsuz.yamm.presentation.baseui.ScopedMviController
import com.dimsuz.yamm.presentation.baseui.util.activityUnsafe
import com.dimsuz.yamm.presentation.baseui.util.appScope
import com.dimsuz.yamm.presentation.baseui.util.isVisible
import com.dimsuz.yamm.presentation.navdrawer.context.base.DrawerContextType
import com.dimsuz.yamm.presentation.navdrawer.context.base.NavDrawerContextManager
import com.dimsuz.yamm.util.instance
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.messages.*
import toothpick.config.Module
import java.util.concurrent.TimeUnit

internal class MessagesController
  : ScopedMviController<Messages.ViewState, Messages.View, MessagesPresenter>(),
  Messages.View {

  override fun createScopedConfig() = object : Config {
    override val viewLayoutResource = R.layout.messages
    override val screenModule = object : Module() {
      init {
        bind(MessagesPresenter::class.java).to(MessagesPresenter::class.java)
      }
    }
  }

  private var messagesAdapter: MessagesAdapter = MessagesAdapter()
  private var foregroundStateChanges: BehaviorSubject<Boolean> = BehaviorSubject.create()

  override fun createPresenter(): MessagesPresenter {
    return screenScope.instance()
  }

  override fun initializeView(rootView: View) {
    appScope.instance<NavDrawerContextManager>().setContext(DrawerContextType.Messages)
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

  override fun postInputTextChangedIntent(): Observable<String> {
    return sendMessageTextView
      .textChanges()
      .debounce(300, TimeUnit.MILLISECONDS)
      .map { it.toString() }
  }

  override fun sendPostIntent(): Observable<String> {
    return sendMessageButton.clicks()
      .map { sendMessageTextView.text.toString() }
      // if sendMessageTextView content will start being set from viewState on each text change,
      // remove this line - it should be taken care of by view state. for now going a simple route
      .doOnNext { sendMessageTextView.setText("") }
  }

  override fun renderViewState(viewState: Messages.ViewState) {
    if (previousViewState?.posts != viewState.posts) {
      messagesAdapter.setData(viewState.posts)
    }
    if (viewState.postDraft != null) {
      sendMessageTextView.setText(viewState.postDraft)
    }

    sendMessageButton.isVisible = viewState.sendButtonVisible
    attachFileButton.isVisible = viewState.attachButtonVisible
  }

}
