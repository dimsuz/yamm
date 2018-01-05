package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.core.util.checkMainThread
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractorImpl.Request
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractorImpl.Result
import com.dimsuz.yamm.domain.interactors.ChannelPostsInteractorImpl.State
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.models.ServerEvent
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.PostRepository
import com.dimsuz.yamm.domain.repositories.ServerEventRepository
import com.dimsuz.yamm.domain.util.AppSchedulers
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

internal const val DEFAULT_CHANNEL_NAME = "town-square"

internal class ChannelPostsInteractorImpl @Inject constructor(
  private val postRepository: PostRepository,
  private val channelRepository: ChannelRepository,
  private val serverEventRepository: ServerEventRepository,
  schedulers: AppSchedulers,
  private val logger: Logger)
  : ReactiveInteractor<State, Request, Result>(State(), logger, schedulers), ChannelPostsInteractor {

  private val stateEvents = BehaviorSubject.create<ChannelPostEvent>().toSerialized()
  private var foregroundChangesSubscription = Disposables.disposed()

  override fun stateEvents(): Observable<ChannelPostEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  override fun setChannel(channelId: String) {
    scheduleRequest(Request.SetChannelId(channelId))
  }

  fun setDefaultChannel(userId: String, teamId: String) {
    val defaultChannelId = channelRepository.getChannelIdByName(DEFAULT_CHANNEL_NAME, teamId)
      ?: channelRepository.getChannelIds(userId, teamId).firstOrNull().also { logNoDefaultChannel() }
      ?: throw IllegalStateException("failed to get default channel: team has no channels")
    setChannel(defaultChannelId)
  }

  override fun addPost(message: String) {
    scheduleRequest(Request.SendPost(message))
  }

  override fun loadMoreMessages() {
    scheduleRequest(Request.LoadMoreMessages())
  }

  override fun setForegroundStateChanges(changes: Observable<Boolean>) {
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

  override fun resetForegroundStateChanges() {
    foregroundChangesSubscription.dispose()
  }

  override fun createCommand(request: Request, state: State): Observable<Result> {
    return when (request) {
      is Request.SetChannelId -> createSetChannelIdCommand(request)
      is Request.SendPost -> createSendPostCommand(request, state)
      is Request.LoadMoreMessages -> createLoadMoreMessagesCommand(state)
    }
  }

  override fun reduceState(previousState: State, commandResult: Result): State {
    return when (commandResult) {
      is Result.QueryChanged -> previousState.copy(query = commandResult.query)
    }
  }


  override fun channelPosts(): Observable<List<Post>> {
    checkMainThread()
    return stateChanges.filter { it.query != null }
      .switchMap { state ->
        with(state.query!!) {
          logger.debug("query changed $this")
          postRepository.postsLive(channelId, firstPage, lastPage, pageSize)
        }
      }
      .doOnError { logger.error(it, "failed to read db") }
  }

  private fun createSetChannelIdCommand(request: Request.SetChannelId): Observable<Result> {
    return Single.just(QueryState(request.channelId))
      .flatMapObservable { query -> createRunQueryOperation(query) }
  }

  private fun createSendPostCommand(request: Request.SendPost, state: State): Observable<Result> {
    return Single
      .fromCallable {
        state.query?.channelId ?: throw IllegalStateException("expected channelId to be set when sending post")
      }
      .flatMapCompletable { channelId -> postRepository.addNew(channelId, request.message) }
      .toObservable()
  }

  private fun createLoadMoreMessagesCommand(state: State): Observable<Result> {
    return Single
      .fromCallable {
        state.query ?: throw IllegalStateException("expected query to be set when sending post")
      }
      .map { it.copy(lastPage = it.lastPage + 1) }
      .flatMapObservable { query -> createRunQueryOperation(query) }
  }


  private fun createRunQueryOperation(query: QueryState): Observable<Result> {
    // TODO caching is needed here so that posts would be cached only if
    // channel cache is invalidated somehow (either timebased or websocket told us)
    return Completable.fromAction { stateEvents.onNext(ChannelPostEvent.Loading()) }
      .andThen(Completable.defer {
        postRepository
          .fetchPosts(query.channelId, query.firstPage, query.lastPage, query.pageSize)
          .andThen(Completable.fromAction { stateEvents.onNext(ChannelPostEvent.Idle()) })
          .onErrorResumeNext({ e -> Completable.fromAction { stateEvents.onNext(ChannelPostEvent.LoadFailed(e)) } })
      })
      .andThen(Observable.just<Result>(Result.QueryChanged(query)))
  }

  private fun processServerEvent(event: ServerEvent) {
    when (event) {
      is ServerEvent.UserTyping -> logger.debug("user typing!")
      is ServerEvent.Posted -> logger.debug("got post event!")
      is ServerEvent.Unknown -> logger.debug("unknown event")
    }
  }

  private fun logNoDefaultChannel() {
    logger.error("team has no default channel, getting a first one available")
  }

  internal sealed class Request {
    data class SetChannelId(val channelId: String) : Request()
    data class SendPost(val message: String): Request()
         class LoadMoreMessages : Request()
  }

  internal sealed class Result {
    data class QueryChanged(val query: QueryState): Result()
  }

  internal data class State(val query: QueryState? = null)
}

internal data class QueryState(
  val channelId: String,
  val firstPage: Int = 0,
  val lastPage: Int = 0,
  val pageSize: Int = 60)
