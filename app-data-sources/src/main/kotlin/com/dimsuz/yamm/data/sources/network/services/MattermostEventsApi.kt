package com.dimsuz.yamm.data.sources.network.services

import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import timber.log.Timber

sealed class WebSocketEvent {
  object Open : WebSocketEvent()
  data class Closing(val code: Int, val reason: String) : WebSocketEvent()
  data class TextMessage(val text: String) : WebSocketEvent()
  data class BinaryMessage(val bytes: ByteString) : WebSocketEvent()
}

class MattermostEventsApi(
  private val client: OkHttpClient,
  private val moshi: Moshi,
  private val serverUrl: String) {

  @Volatile
  private var socket: WebSocket? = null

  fun createNewEventStream(): Observable<WebSocketEvent> {
    if (socket != null) {
      throw IllegalStateException("createNewEventStream() is called while existing web socket is active")
    }

    return Observable.create<WebSocketEvent> { emitter: ObservableEmitter<WebSocketEvent> ->
      val request = Request.Builder()
        .url("$serverUrl/api/v4/websocket")
        .build()
      socket = client.newWebSocket(request, createSocketListener(emitter))
    }
  }

  fun sendMessage(message: Any) {
    if (socket == null) throw IllegalStateException("socket is not created, call createNewEventStream() first")
    // TODO do this json encoding on a background thread?!
    val text = moshi.adapter(message.javaClass).toJson(message)
    socket?.send(text)
  }

  fun closeConnection() {
    socket?.close(0, "Bye")
    socket = null
  }

  fun cancelConnection() {
    socket?.cancel()
    socket = null
  }

  private fun createSocketListener(emitter: ObservableEmitter<WebSocketEvent>): WebSocketListener {
    return object : WebSocketListener() {
      override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("web socket opened")
        emitter.onNext(WebSocketEvent.Open)
      }

      override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response) {
        Timber.d(t, "web socket failure")
        emitter.onError(t)
      }

      override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Timber.d("web socket closing, code=$code, reason=$reason")
        emitter.onNext(WebSocketEvent.Closing(code, reason))
      }

      override fun onMessage(webSocket: WebSocket, text: String) {
        Timber.d("web socket text message received: $text")
        if (!emitter.isDisposed) {
          emitter.onNext(WebSocketEvent.TextMessage(text))
        } else {
          Timber.d("downstream has disposed subscription, disconnecting from socket")
          webSocket.cancel()
        }
      }

      override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Timber.d("web socket binary message of length ${bytes.size()} received")
        if (!emitter.isDisposed) {
          emitter.onNext(WebSocketEvent.BinaryMessage(bytes))
        } else {
          Timber.d("downstream has disposed subscription, disconnecting from socket")
          webSocket.cancel()
        }
      }

      override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Timber.d("web socket closed, code=$code, reason=$reason")
        emitter.onComplete()
      }
    }
  }
}