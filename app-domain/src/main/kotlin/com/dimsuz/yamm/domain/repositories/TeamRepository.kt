package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Team
import io.reactivex.Single

interface TeamRepository {
  fun userTeams(userId: String): Single<List<Team>>
}