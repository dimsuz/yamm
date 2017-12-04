package com.dimsuz.yamm.domain.models

sealed class ServerEvent {
  object UserTyping : ServerEvent()
  object Posted : ServerEvent()
}
