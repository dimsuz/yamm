package com.dimsuz.yamm.domain.models

sealed class ServerEvent {
  object UserTyping
  object Posted
}
