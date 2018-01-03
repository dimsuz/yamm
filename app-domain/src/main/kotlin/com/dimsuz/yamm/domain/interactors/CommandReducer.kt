package com.dimsuz.yamm.domain.interactors

internal class ChannelPostsCommandReducer {
  private var query: QueryState? = null

  fun handleEvent(event: InputEvent): Command? {
    return when (event) {
      is InputEvent.ChannelIdChanged -> {
        query = QueryState(event.channelId)
        Command.RunQuery(query!!)
      }
      is InputEvent.NextPageRequested -> {
        val oldQuery = query
          ?: throw IllegalStateException("called loadAnotherPage when first is not loaded")
        query = oldQuery.copy(lastPage = oldQuery.lastPage + 1)
        Command.RunQuery(query!!)
      }
      is InputEvent.SendPostRequested -> {
        val channelId = query?.channelId ?: throw IllegalStateException("cannot send post when channel is not set")
        Command.SendPost(channelId, event.message)
      }
    }
  }

  internal sealed class InputEvent {
    data class ChannelIdChanged(val channelId: String) : InputEvent()
    class NextPageRequested : InputEvent()
    data class SendPostRequested(val message: String): InputEvent()
  }

  internal sealed class Command {
    data class RunQuery(val query: QueryState): Command()
    data class SendPost(val channelId: String, val message: String): Command()
  }

}

internal data class QueryState(
  val channelId: String,
  val firstPage: Int = 0,
  val lastPage: Int = 0,
  val pageSize: Int = 60)
