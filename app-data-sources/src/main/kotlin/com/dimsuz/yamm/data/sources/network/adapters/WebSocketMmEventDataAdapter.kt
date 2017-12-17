package com.dimsuz.yamm.data.sources.network.adapters

import com.dimsuz.yamm.data.sources.network.models.WebSocketMmEvent
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import timber.log.Timber

internal class WebSocketMmEventDataAdapter : JsonAdapter<WebSocketMmEvent>() {
  lateinit var moshi: Moshi

  @ToJson
  override fun toJson(writer: JsonWriter, value: WebSocketMmEvent?) {
    TODO("not implemented")
  }

  @FromJson
  override fun fromJson(reader: JsonReader): WebSocketMmEvent {
    var eventType: String? = null
    var data: WebSocketMmEvent.Data? = null
    var seq: Int? = null
    reader.beginObject()
    while (reader.hasNext()) {
      when (reader.nextName()) {
        "event" -> {
          eventType = reader.nextString()
        }
        "data" -> {
          if (eventType != null) {
            data = parseEventData(eventType, reader)
          }
          if (data == null) {
            if (eventType != null) {
              Timber.d("failed to read data for event $eventType: unsupported or malformed")
            }
            reader.skipValue()
          }
        }
        "seq" -> {
          seq = reader.nextInt()
        }
        else -> {
          reader.skipValue()
        }
      }
    }
    reader.endObject()
    return WebSocketMmEvent(eventType, data, seq)
  }

  private fun parseEventData(event: String, reader: JsonReader): WebSocketMmEvent.Data? {
    val dataClass = WebSocketMmEvent.EVENT_MAPPING[event] ?: return null
    val dataAdapter = moshi.adapter(dataClass)
    return dataAdapter.fromJson(reader)
  }
}

