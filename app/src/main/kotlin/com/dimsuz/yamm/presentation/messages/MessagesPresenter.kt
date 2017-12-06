package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.domain.interactors.ChannelPostEvent
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.presentation.baseui.BaseMviPresenter
import com.dimsuz.yamm.presentation.baseui.RoutingAction
import com.dimsuz.yamm.util.AppSchedulers
import com.dimsuz.yamm.util.ErrorDetailsExtractor
import io.reactivex.Observable
import javax.inject.Inject

sealed class ScreenEvent
private data class PostListChanged(val posts: List<Post>) : ScreenEvent()
private data class PostListLoadError(val error: Throwable) : ScreenEvent()
private      class PostListLoading : ScreenEvent()
private      class PostListLoadFinished : ScreenEvent()

class MessagesPresenter @Inject constructor(
  schedulers: AppSchedulers,
  private val channelPostsInteractor: ChannelPostsInteractor,
  private val errorDetailsExtractor: ErrorDetailsExtractor
) : BaseMviPresenter<Messages.View, Messages.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    val postsListChanges = channelPostsInteractor.channelPosts()
      .map(::PostListChanged)
    val postStateChanges = channelPostsInteractor.stateEvents()
      .map(this::postEventToScreenEvent)
    return listOf(postsListChanges, postStateChanges)
  }

  override fun viewStateReducer(previousState: Messages.ViewState,
                                event: ScreenEvent): Messages.ViewState {
    return when (event) {
      is PostListChanged -> {
        previousState.copy(posts = event.posts)
      }
      is PostListLoading -> {
        previousState.clearTransientState().copy(showProgressBar = true)
      }
      is PostListLoadFinished -> {
        previousState.clearTransientState()
      }
      is PostListLoadError -> {
        previousState.copy(contentLoadingError = errorDetailsExtractor.extractErrorText(event.error))
      }
    }
  }

  override fun routingStateReducer(previousState: Messages.ViewState, newState: Messages.ViewState,
                                   event: ScreenEvent): RoutingAction? {
    return null
  }

  override fun createInitialState(): Messages.ViewState {
    return Messages.ViewState(
      posts = emptyList(),
      contentLoadingError = null,
      showProgressBar = false
    )
  }

  private fun postEventToScreenEvent(event: ChannelPostEvent): ScreenEvent {
    return when (event) {
      is ChannelPostEvent.Idle -> PostListLoadFinished()
      is ChannelPostEvent.Loading -> PostListLoading()
      is ChannelPostEvent.LoadFailed -> PostListLoadError(event.error)
      is ChannelPostEvent.LiveConnectionFailed -> TODO("not implemented")
    }
  }
}