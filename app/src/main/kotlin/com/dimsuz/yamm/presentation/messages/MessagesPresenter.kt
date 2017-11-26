package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.presentation.baseui.BaseMviPresenter
import com.dimsuz.yamm.presentation.baseui.RoutingAction
import com.dimsuz.yamm.util.AppSchedulers
import io.reactivex.Observable
import javax.inject.Inject

sealed class ScreenEvent
private data class PostListChanged(val posts: List<Post>) : ScreenEvent()

class MessagesPresenter @Inject constructor(
  schedulers: AppSchedulers,
  private val channelPostsInteractor: ChannelPostsInteractor
) : BaseMviPresenter<Messages.View, Messages.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    val postsListChanges = channelPostsInteractor.channelPosts()
      .map(::PostListChanged)
    return listOf(postsListChanges)
  }

  override fun viewStateReducer(previousState: Messages.ViewState, event: ScreenEvent): Messages.ViewState {
    return previousState
  }

  override fun routingStateReducer(previousState: Messages.ViewState, newState: Messages.ViewState, event: ScreenEvent): RoutingAction? {
    return null
  }

  override fun createInitialState(): Messages.ViewState {
    return Messages.ViewState(
      posts = emptyList(),
      contentLoadingError = null,
      showProgressBar = false
    )
  }
}