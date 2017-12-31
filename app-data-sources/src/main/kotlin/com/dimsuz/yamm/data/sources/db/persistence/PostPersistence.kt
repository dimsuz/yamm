package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.PostDbModel
import com.dimsuz.yamm.data.sources.db.models.PostWithUserDbModel
import io.reactivex.Observable

interface PostPersistence {
  fun replacePosts(posts: List<PostDbModel>)
  fun getPostsLive(channelId: String, offset: Int, count: Int): Observable<List<PostWithUserDbModel>>
}