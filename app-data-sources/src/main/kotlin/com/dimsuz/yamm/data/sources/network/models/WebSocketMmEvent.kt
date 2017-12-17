package com.dimsuz.yamm.data.sources.network.models

import com.dimsuz.yamm.data.sources.network.adapters.EscapedPostJson

data class WebSocketMmEvent(
  val event: String?,
  val data: Data?,
  val seq: Int?
) {

  sealed class Data {

    data class Posted(
      val team_id: String?,
      val channel_name: String?,
      @field:EscapedPostJson
      val post: PostJson?) : Data()

    data class ChannelViewed(
      val channel_id: String?
    ) : Data()

    // NOTE: when adding a new data type, update `eventMapping` below
  }

  internal companion object {
    val EVENT_MAPPING = mapOf(
      "posted" to Data.Posted::class.java,
      "channel_viewed" to Data.ChannelViewed::class.java)
  }
}