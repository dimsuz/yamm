package com.dimsuz.yamm.data.sources.db.persistence

import com.dimsuz.yamm.data.sources.db.models.PostDbModel

class PostPersistenceImpl : PostPersistence {
  override fun replacePosts(posts: List<PostDbModel>) {
    TODO("not implemented")
  }

  override fun getPostsLive(channelId: String, offset: Int, count: Int): List<PostDbModel> {
    TODO("not implemented")
  }
}