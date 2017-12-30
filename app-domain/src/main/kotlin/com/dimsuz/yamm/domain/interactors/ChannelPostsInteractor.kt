package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.core.util.checkMainThread
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.models.ServerEvent
import com.dimsuz.yamm.domain.repositories.PostRepository
import com.dimsuz.yamm.domain.repositories.ServerEventRepository
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

sealed class ChannelPostEvent {
       class Loading : ChannelPostEvent()
  data class LoadFailed(val error: Throwable) : ChannelPostEvent()
       class Idle : ChannelPostEvent()
  data class LiveConnectionFailed(val error: Throwable) : ChannelPostEvent()
}

class ChannelPostsInteractor @Inject constructor(
  private val postRepository: PostRepository,
  private val serverEventRepository: ServerEventRepository,
  private val logger: Logger) {

  private val queryChanges = BehaviorSubject.create<QueryState>().toSerialized()
  private val stateEvents = BehaviorSubject.create<ChannelPostEvent>().toSerialized()
  private var foregroundChangesSubscription = Disposables.disposed()

  fun stateEvents(): Observable<ChannelPostEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  fun setChannel(channelId: String) {
    checkMainThread()
    logger.debug("switching current channel to $channelId")
    updateQueryState(QueryState(channelId = channelId))
  }

  fun setForegroundStateChanges(changes: Observable<Boolean>) {
    if (!foregroundChangesSubscription.isDisposed) {
      // TODO maybe this needs to be relaxed? but then need to think of how properly disconnect old connection
      // (disposing it might not disconnect right away, maybe some explicit method on repository, like disconnect()
      // (and then dispose)
      throw RuntimeException("setForegroundStateChanges() must be called only once after creation")
    }
    foregroundChangesSubscription = serverEventRepository.serverEventsLive(changes)
      .subscribe(
        { processServerEvent(it) },
        { stateEvents.onNext(ChannelPostEvent.LiveConnectionFailed(it)) }
      )
  }

  fun resetForegroundStateChanges() {
    foregroundChangesSubscription.dispose()
  }

  fun loadAnotherPage() {
    checkMainThread()
    val oldQuery = queryChanges.take(1).blockingFirst(null)
      ?: throw IllegalStateException("called loadAnotherPage when first is not loaded")
    val newQuery = oldQuery.copy(lastPage = oldQuery.lastPage + 1)
    updateQueryState(newQuery)
  }

  fun channelPosts(): Observable<List<Post>> {
    checkMainThread()
    return queryChanges
      .switchMap { queryState ->
        with(queryState) {
          logger.debug("query changed $queryState")
          postRepository.postsLive(channelId, firstPage, lastPage, pageSize)
        }
      }
      .doOnError { logger.error(it, "failed to read db") }
  }

  private fun updateQueryState(newState: QueryState) {
    queryChanges.onNext(newState)

    // TODO caching is needed here so that posts would be cached only if
    // channel cache is invalidated somehow (either timebased or websocket told us)
    stateEvents.onNext(ChannelPostEvent.Loading())
    postRepository
      .fetchPosts(newState.channelId, newState.firstPage, newState.lastPage, newState.pageSize)
      .subscribe(
        { stateEvents.onNext(ChannelPostEvent.Idle()) },
        { stateEvents.onNext(ChannelPostEvent.LoadFailed(it)) }
      )
  }

  private fun processServerEvent(event: ServerEvent) {
    when (event) {
      is ServerEvent.UserTyping -> logger.debug("user typing!")
      is ServerEvent.Posted -> {
        postRepository.insert(event.post)
      }
      is ServerEvent.Unknown -> logger.debug("unknown event")
    }
  }

}

private data class QueryState(
  val channelId: String,
  val firstPage: Int = 0,
  val lastPage: Int = 0,
  val pageSize: Int = 60)