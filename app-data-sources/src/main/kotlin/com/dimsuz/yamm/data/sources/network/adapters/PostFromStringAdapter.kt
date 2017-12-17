package com.dimsuz.yamm.data.sources.network.adapters

import com.dimsuz.yamm.data.sources.network.models.PostJson
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

@JsonQualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class EscapedPostJson

class PostFromStringAdapter {
  lateinit var moshi: Moshi

  @ToJson
  fun postJsonToString(@EscapedPostJson postJson: PostJson): String {
    throw NotImplementedError()
  }

  @FromJson
  @EscapedPostJson
  fun postFromString(postJson: String): PostJson? {
    return moshi.adapter(PostJson::class.java).fromJson(postJson)
  }

}