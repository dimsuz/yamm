package com.dimsuz.yamm.repositories

import com.dimsuz.yamm.core.util.checkOrLog
import com.dimsuz.yamm.data.sources.db.models.PostWithUserDbModel
import com.dimsuz.yamm.data.sources.db.persistence.PostPersistence
import com.dimsuz.yamm.data.sources.network.models.PostCreateParams
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.domain.models.Post
import com.dimsuz.yamm.domain.repositories.PostRepository
import com.dimsuz.yamm.domain.repositories.UserRepository
import com.dimsuz.yamm.repositories.mappers.toDatabaseModel
import com.dimsuz.yamm.repositories.mappers.toDomainModel
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class PostRepositoryImpl @Inject constructor(
  private val serviceApi: MattermostAuthorizedApi,
  private val persistence: PostPersistence,
  private val userRepository: UserRepository) : PostRepository {

  override fun fetchPosts(channelId: String, firstPage: Int, lastPage: Int, pageSize: Int): Completable {
    check(firstPage <= lastPage)
    return serviceApi.getChannelPosts(
      channelId = channelId,
      page = firstPage,
      perPage = (lastPage - firstPage + 1) * pageSize,
      sinceTimestamp = null,
      beforePostId = null,
      afterPostId = null)
      .map { it.posts.values }
      .flatMapCompletable { postList ->
        val validPosts = postList.filter { it.user_id != null }
        val postUserIds = validPosts.mapNotNullTo(hashSetOf()) { it.user_id }
        checkOrLog(validPosts.size == postList.size) {
          "post fetch resulted in ${postList.size - validPosts.size} invalid posts, skipping them"
        }
        // each post must be saved only along with corresponding user object,
        // ensure that they are present
        userRepository.refreshUsers(postUserIds)
          .andThen(
            Completable.fromAction {
              val postsDatabase = postList.map { it.toDatabaseModel() }
              persistence.replacePosts(postsDatabase)
            })
      }
  }

  override fun addNew(channelId: String, message: String): Completable {
    // adding post should result in web socket event which would auto-insert it.
    // so ignoring api result here hoping it would work
    // (can reiterate this later and insert result in DB if this strategy fails)
    return serviceApi.createPost(PostCreateParams(channelId, message, null, null, null))
      .toCompletable()
  }

  override fun postsLive(channelId: String, firstPage: Int, lastPage: Int, pageSize: Int): Observable<List<Post>> {
    check(firstPage <= lastPage)
    return persistence.getPostsLive(
      channelId = channelId,
      offset = firstPage * pageSize,
      count = (lastPage - firstPage + 1) * pageSize)
      .map { it.map(PostWithUserDbModel::toDomainModel) }
  }
}