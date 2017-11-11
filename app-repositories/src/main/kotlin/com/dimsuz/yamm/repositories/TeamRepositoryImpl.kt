package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Team
import com.dimsuz.yamm.domain.repositories.TeamRepository
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Single
import javax.inject.Inject

internal class TeamRepositoryImpl @Inject internal constructor(private val serviceApi: MattermostAuthorizedApi) : TeamRepository {

  override fun userTeams(userId: String): Single<List<Team>> {
    return serviceApi.getUserTeams(userId)
      .map { teams -> teams.map { it.toDomainModel() } }
  }

}