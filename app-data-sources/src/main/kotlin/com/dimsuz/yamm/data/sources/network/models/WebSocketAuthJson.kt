package com.dimsuz.yamm.data.sources.network.models

data class WebSocketAuthJson(
  val seq: Int = 1,
  val action: String = "authentication_challenge",
  val data: Map<String, String>) {

  constructor(token: String) : this(data = mapOf("token" to token))

}