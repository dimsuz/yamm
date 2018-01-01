package com.dimsuz.yamm.data.sources.di

import android.content.Context
import com.dimsuz.yamm.core.annotations.ApplicationContext
import com.dimsuz.yamm.core.annotations.BaseUrl
import com.dimsuz.yamm.core.annotations.ServerUrl
import com.dimsuz.yamm.core.log.Logger
import com.dimsuz.yamm.data.BuildConfig
import com.dimsuz.yamm.data.sources.db.DatabaseHelper
import com.dimsuz.yamm.data.sources.db.persistence.ChannelPersistence
import com.dimsuz.yamm.data.sources.db.persistence.ChannelPersistenceImpl
import com.dimsuz.yamm.data.sources.db.persistence.PostPersistence
import com.dimsuz.yamm.data.sources.db.persistence.PostPersistenceImpl
import com.dimsuz.yamm.data.sources.db.persistence.UserPersistence
import com.dimsuz.yamm.data.sources.db.persistence.UserPersistenceImpl
import com.dimsuz.yamm.data.sources.network.adapters.PostFromStringAdapter
import com.dimsuz.yamm.data.sources.network.adapters.WebSocketMmEventDataAdapter
import com.dimsuz.yamm.data.sources.network.services.MattermostAuthorizedApi
import com.dimsuz.yamm.data.sources.network.services.MattermostEventsApi
import com.dimsuz.yamm.data.sources.network.services.MattermostPublicApi
import com.dimsuz.yamm.data.sources.network.session.SessionTokenAddInterceptor
import com.squareup.moshi.Moshi
import com.squareup.sqlbrite2.BriteDatabase
import com.squareup.sqlbrite2.SqlBrite
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import toothpick.config.Module
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Provider

private const val DEFAULT_CONNECT_TIMEOUT = 90000L
private const val API_VERSION = "v4"

/**
 * Binds dependencies which can be used without having access to a configured serverUrl.
 *
 * Done in a form of a callable function rather than a full-blown Module sub-class to avoid
 * the need to leak dependency to `app-data-sources` to other modules: only `app-repositories` module
 * needs to depend on `app-data-sources`
 */
fun bindDataSourcesCommonDependencies(module: Module) {
  with(module) {
    bind(Moshi::class.java).toInstance(createMoshi())
  }
}

/**
 * Binds dependencies which can be used only when having a configured serverUrl.
 *
 * Done in a form of a callable function rather than a full-blown Module sub-class to avoid
 * the need to leak dependency to `app-data-sources` to other modules: only `app-repositories` module
 * needs to depend on `app-data-sources`
 */
fun bindDataSourcesDependencies(module: Module, serverUrl: String) {
  with(module) {
    bind(String::class.java).withName(ServerUrl::class.java).toInstance(serverUrl)
    bind(String::class.java).withName(BaseUrl::class.java).toInstance(createBaseUrl(serverUrl))
    bind(Interceptor::class.java).to(SessionTokenAddInterceptor::class.java).singletonInScope()
    bind(OkHttpClient::class.java).toProvider(HttpClientProvider::class.java).providesSingletonInScope()
    bind(MattermostAuthorizedApi::class.java).toProvider(MattermostAuthorizedApiProvider::class.java).providesSingletonInScope()
    bind(MattermostEventsApi::class.java).toProvider(MattermostEventsApiProvider::class.java).providesSingletonInScope()
    // expecting to use this rarely, so should be GCed after use...
    bind(MattermostPublicApi::class.java).toProvider(MattermostPublicApiProvider::class.java)

    bind(BriteDatabase::class.java).toProvider(BriteDatabaseProvider::class.java).providesSingletonInScope()
    bind(UserPersistence::class.java).to(UserPersistenceImpl::class.java)
    bind(ChannelPersistence::class.java).to(ChannelPersistenceImpl::class.java)
    bind(PostPersistence::class.java).to(PostPersistenceImpl::class.java)
  }
}

internal class BriteDatabaseProvider
@Inject constructor(@ApplicationContext private val context: Context,
            private val logger: Logger) : Provider<BriteDatabase> {

  override fun get(): BriteDatabase {
    return SqlBrite.Builder()
      .logger { logger.debug(it) }
      .build()
      .wrapDatabaseHelper(DatabaseHelper(context), Schedulers.io())
//      .apply { setLoggingEnabled(BuildConfig.DEBUG) }
  }
}

internal class HttpClientProvider
@Inject constructor(private val authSessionInterceptor: Interceptor): Provider<OkHttpClient> {
  override fun get(): OkHttpClient {
    return createHttpClient(listOf(authSessionInterceptor))
  }
}

internal class MattermostAuthorizedApiProvider
@Inject constructor(private val httpClient: OkHttpClient,
                    private val moshi: Moshi,
                    @ServerUrl private val serverUrl: String): Provider<MattermostAuthorizedApi> {

  override fun get(): MattermostAuthorizedApi {
    Timber.e("creating server api with a server url: $serverUrl")
    return createMattermostApi(httpClient, moshi, serverUrl)
  }

}

internal class MattermostEventsApiProvider
@Inject constructor(private val httpClient: OkHttpClient,
                    private val moshi: Moshi,
                    @ServerUrl private val serverUrl: String): Provider<MattermostEventsApi> {
  override fun get(): MattermostEventsApi {
    return MattermostEventsApi(httpClient, moshi, serverUrl)
  }
}

internal class MattermostPublicApiProvider
@Inject constructor(private val moshi: Moshi,
                    @ServerUrl private val serverUrl: String): Provider<MattermostPublicApi> {

  override fun get(): MattermostPublicApi {
    Timber.e("creating public api with a server url: $serverUrl")
    return createMattermostApi(createHttpClient(), moshi, serverUrl)
  }
}


private fun createHttpClient(interceptors: List<Interceptor> = emptyList()): OkHttpClient {
  val builder = OkHttpClient.Builder()
    .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)

  // add logging interceptor last, so it would see everything added by a previous ones
  interceptors.plus(createLoggingInterceptor()).forEach { builder.addNetworkInterceptor(it) }
  return builder.build()
}

private fun createLoggingInterceptor(): HttpLoggingInterceptor {
  val logLevel = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BASIC
  val logger = { s: String -> Timber.tag("YammNetwork"); Timber.d(s) }
  val interceptor = HttpLoggingInterceptor(logger)
  interceptor.level = logLevel
  return interceptor
}

private fun createMoshi(): Moshi {
  val webSocketMmEventDataAdapter = WebSocketMmEventDataAdapter()
  val postFromStringAdapter = PostFromStringAdapter()
  val moshi = Moshi.Builder()
    .add(webSocketMmEventDataAdapter)
    .add(postFromStringAdapter)
    .build()
  webSocketMmEventDataAdapter.moshi = moshi
  postFromStringAdapter.moshi = moshi
  return moshi
}

private inline fun <reified T> createMattermostApi(httpClient: OkHttpClient,
                                                   moshi: Moshi,
                                                   serverUrl: String): T {
  val callAdapterFactory = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
  val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(callAdapterFactory)
    .client(httpClient)
    .baseUrl(createBaseUrl(serverUrl))
    .build()
  return retrofit.create(T::class.java)
}

private fun createBaseUrl(serverUrl: String) = "$serverUrl/api/$API_VERSION/"
