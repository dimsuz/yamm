package com.dimsuz.yamm.data.repositories

import com.dimsuz.yamm.data.sources.network.mappers.toDomainModel
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Team
import io.reactivex.Observable
import javax.inject.Inject

class TeamRepository @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) {

  fun userTeams(userId: String): Observable<List<Team>> {
    return serviceApi.getUserTeams(userId)
      .map { teams -> teams.map { it.toDomainModel() } }
      .toObservable()
  }

}