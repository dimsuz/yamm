package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.repositories.PostRepository
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

sealed class ChannelPostEvent {
       class Loading : ChannelPostEvent()
  data class LoadFailed(val error: Throwable) : ChannelPostEvent()
       class Idle : ChannelPostEvent()
}

class ChannelPostsInteractor @Inject constructor(
  private val postRepository: PostRepository,
  private val logger: Logger) {

  private val resubscribeTrigger = PublishSubject.create<Unit>()
  @Volatile
  private var queryState: QueryState? = null
  private val stateEvents = BehaviorSubject.create<ChannelPostEvent>().toSerialized()

  fun stateEvents(): Observable<ChannelPostEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  fun setChannel(channelId: String) {
    logger.checkMainThread()
    logger.debug("switching current channel to $channelId")
    updateQueryState(QueryState(channelId = channelId))
  }

  fun loadAnotherPage() {
    logger.checkMainThread()
    val oldQuery = queryState
      ?: throw IllegalStateException("called loadAnotherPage when first is not loaded")
    val newQuery = oldQuery.copy(lastPage = oldQuery.lastPage + 1)
    updateQueryState(newQuery)
  }

  fun channelPosts(): Observable<List<Post>> {
    logger.checkMainThread()
    return Observable.fromCallable { queryState }
      .flatMap { queryState ->
        with (queryState) {
          postRepository.postsLive(channelId, firstPage, lastPage, pageSize)
        }
      }
      .takeUntil<Unit> { resubscribeTrigger }
      .repeat()
  }

  private fun updateQueryState(newState: QueryState) {
    queryState = newState
    resubscribeTrigger.onNext(Unit)

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

}

private data class QueryState(
  val channelId: String,
  val firstPage: Int = 0,
  val lastPage: Int = 0,
  val pageSize: Int = 60)