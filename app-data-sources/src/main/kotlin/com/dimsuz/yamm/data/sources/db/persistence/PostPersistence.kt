package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import io.reactivex.Observable

interface PostPersistence {
  fun replacePosts(posts: List<PostDbModel>)
  fun getPostsLive(channelId: String, offset: Int, count: Int): Observable<List<PostDbModel>>
}