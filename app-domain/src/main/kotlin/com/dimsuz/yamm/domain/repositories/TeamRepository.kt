package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Team
import io.reactivex.Observable

interface TeamRepository {
  fun userTeams(userId: String): Observable<List<Team>>
}