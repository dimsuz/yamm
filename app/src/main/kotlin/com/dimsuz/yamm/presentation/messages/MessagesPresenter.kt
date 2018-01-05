package com.dimsuz.yamm.presentation.messages

import com.dimsuz.yamm.domain.interactors.ChannelPostEvent
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractor
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.presentation.baseui.BaseMviPresenter
import com.dimsuz.yamm.presentation.baseui.RoutingAction
import com.dimsuz.yamm.domain.util.AppSchedulers
import com.dimsuz.yamm.util.ErrorDetailsExtractor
import io.reactivex.Observable
import javax.inject.Inject

internal sealed class ScreenEvent

private data class PostListChanged(val posts: List<Post>) : ScreenEvent()
private data class PostListLoadError(val error: Throwable) : ScreenEvent()
private      class PostListLoading : ScreenEvent()
private      class PostListLoadFinished : ScreenEvent()
private data class LiveConnectionError(val error: Throwable) : ScreenEvent()
private data class PostInputEmptyStateChanged(val isEmpty: Boolean) : ScreenEvent()
private      class PostSendSuccess : ScreenEvent()

internal class MessagesPresenter @Inject constructor(
  schedulers: AppSchedulers,
  private val channelPostsInteractor: ChannelPostsInteractor,
  private val errorDetailsExtractor: ErrorDetailsExtractor
) : BaseMviPresenter<Messages.View, Messages.ViewState, ScreenEvent>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenEvent>> {
    val postsListChanges = intent { channelPostsInteractor.channelPosts() }
      .map(::PostListChanged)
    val postStateChanges = intent { channelPostsInteractor.stateEvents() }
      .map(this::postEventToScreenEvent)
    val inputEmptyStateChanges = intent(Messages.View::postInputTextChangedIntent)
      .map { it.isEmpty() }
      .distinctUntilChanged()
      .map { PostInputEmptyStateChanged(it) }
    val sendPostClicks = intent(Messages.View::sendPostIntent)
      .doOnNext { channelPostsInteractor.addPost(it) }
      .map { PostSendSuccess() }
    return listOf(postsListChanges, postStateChanges, sendPostClicks, inputEmptyStateChanges)
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
      is LiveConnectionError -> {
        previousState.copy(liveConnectionError = errorDetailsExtractor.extractErrorText(event.error))
      }
      is PostInputEmptyStateChanged -> {
        previousState.copy(
          sendButtonVisible = !event.isEmpty,
          attachButtonVisible = event.isEmpty)
      }
      is PostSendSuccess -> {
        previousState.copy(postDraft = null)
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
      liveConnectionError = null,
      showProgressBar = false,
      postDraft = null,
      sendButtonVisible = false,
      attachButtonVisible = true
    )
  }

  private fun postEventToScreenEvent(event: ChannelPostEvent): ScreenEvent {
    return when (event) {
      is ChannelPostEvent.Idle -> PostListLoadFinished()
      is ChannelPostEvent.Loading -> PostListLoading()
      is ChannelPostEvent.LoadFailed -> PostListLoadError(event.error)
      is ChannelPostEvent.LiveConnectionFailed -> LiveConnectionError(event.error)
    }
  }
}