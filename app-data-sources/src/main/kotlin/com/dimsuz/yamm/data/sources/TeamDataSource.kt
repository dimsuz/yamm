package com.dimsuz.yamm.data.sources

import com.dimsuz.yamm.domain.models.Team
import io.reactivex.Observable

interface TeamDataSource {
  fun teamList(): Observable<List<Team>>
}