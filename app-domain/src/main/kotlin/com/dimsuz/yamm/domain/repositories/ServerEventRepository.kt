package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.ServerEvent
import io.reactivex.Observable

interface ServerEventRepository {
  fun serverEventsLive(connectStateChanges: Observable<Boolean>): Observable<ServerEvent>
}