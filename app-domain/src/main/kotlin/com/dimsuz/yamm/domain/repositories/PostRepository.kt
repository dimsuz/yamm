package com.dimsuz.yamm.domain.repositories

import com.dimsuz.yamm.domain.models.Post
import io.reactivex.Completable
import io.reactivex.Observable

interface PostRepository {
  fun fetchPosts(channelId: String, firstPage: Int, lastPage: Int, pageSize: Int): Completable
  fun postsLive(channelId: String, firstPage: Int, lastPage: Int,
                pageSize: Int): Observable<List<Post>>

  fun insert(post: Post)
}