package com.dimsuz.yamm.domain.interactors

import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.core.util.checkMainThread
import com.dimsuz.yamm.core.util.simpleNameRelative
import com.dimsuz.yamm.domain.interactors.ChannelPostsCommandReducer.Command
import com.dimsuz.yamm.domain.interactors.ChannelPostsCommandReducer.InputEvent
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.models.ServerEvent
import com.dimsuz.yamm.domain.repositories.ChannelRepository
import com.dimsuz.yamm.domain.repositories.PostRepository
import com.dimsuz.yamm.domain.repositories.ServerEventRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

internal const val DEFAULT_CHANNEL_NAME = "town-square"

internal class ChannelPostsInteractorImpl @Inject constructor(
  private val postRepository: PostRepository,
  private val channelRepository: ChannelRepository,
  private val serverEventRepository: ServerEventRepository,
  private val logger: Logger) : ChannelPostsInteractor {

  private val queryChanges = BehaviorSubject.create<QueryState>().toSerialized()
  private val stateEvents = BehaviorSubject.create<ChannelPostEvent>().toSerialized()
  private val commandQueue = PublishSubject.create<Command>().toSerialized()
  private var foregroundChangesSubscription = Disposables.disposed()
  private val commandReducer = ChannelPostsCommandReducer()

  init {
    commandQueue
      .concatMap { executeCommand(it).toObservable<Unit>() }
      .subscribe({}, { throw RuntimeException("commands in queue must not reach error state, implement onError()", it)})
  }

  override fun stateEvents(): Observable<ChannelPostEvent> {
    return stateEvents.doOnNext { logger.debug("state event: $it") }
  }

  override fun setChannel(channelId: String) {
    checkMainThread()
    handleEvent(InputEvent.ChannelIdChanged(channelId))
  }

  fun setDefaultChannel(userId: String, teamId: String) {
    val defaultChannelId = channelRepository.getChannelIdByName(DEFAULT_CHANNEL_NAME, teamId)
      ?: channelRepository.getChannelIds(userId, teamId).firstOrNull().also { logNoDefaultChannel() }
      ?: throw IllegalStateException("failed to get default channel: team has no channels")
    setChannel(defaultChannelId)
  }

  override fun addPost(message: String) {
    handleEvent(InputEvent.SendPostRequested(message))
  }

  override fun loadAnotherPage() {
    checkMainThread()
    handleEvent(InputEvent.NextPageRequested())
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

  override fun channelPosts(): Observable<List<Post>> {
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

  private fun handleEvent(event: InputEvent) {
    Timber.d("handling event: ${event.javaClass.simpleNameRelative}")
    val command = commandReducer.handleEvent(event)
    if (command != null) {
      Timber.d("scheduling new command: ${command.javaClass.simpleNameRelative}")
      commandQueue.onNext(command)
    }
  }

  private fun executeCommand(command: Command): Completable {
    return when (command) {
      is Command.RunQuery -> createRunQueryOperation(command.query)
      is Command.SendPost -> createSendPostOperation(command)
    }
  }

  private fun createRunQueryOperation(query: QueryState): Completable {
    // TODO caching is needed here so that posts would be cached only if
    // channel cache is invalidated somehow (either timebased or websocket told us)
    return Completable
      .fromAction {
        queryChanges.onNext(query)
        stateEvents.onNext(ChannelPostEvent.Loading())
      }
      .andThen(Completable.defer {
        postRepository
          .fetchPosts(query.channelId, query.firstPage, query.lastPage, query.pageSize)
          .andThen(Completable.fromAction { stateEvents.onNext(ChannelPostEvent.Idle()) })
          .onErrorResumeNext({ e -> Completable.fromAction { stateEvents.onNext(ChannelPostEvent.LoadFailed(e)) } })
      })
  }

  private fun createSendPostOperation(command: Command.SendPost): Completable {
    return postRepository.addNew(command.channelId, command.message)
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

}
