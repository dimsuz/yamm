package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Observable
import javax.inject.Inject

class TeamRepository @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) {

  fun userTeams(userId: String): Observable<List<Team>> {
    return serviceApi.getUserTeams(userId)
      .map { teams -> teams.map { it.toDomainModel() } }
      .toObservable()
  }

}