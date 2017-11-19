package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.PostDbModel

interface PostPersistence {
  fun replacePosts(posts: List<PostDbModel>)
  fun getPostsLive(channelId: String, offset: Int, count: Int): List<PostDbModel>
}