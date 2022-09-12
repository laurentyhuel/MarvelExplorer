package com.lyh.marvelexplorer.data.remote.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.lyh.marvelexplorer.data.remote.BuildConfig
import com.lyh.marvelexplorer.data.remote.MarvelApi
import com.lyh.marvelexplorer.data.remote.core.toMD5
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import org.koin.dsl.module
import retrofit2.Retrofit

/**
 * Koin module for data-remote
 */
fun getDataRemoteModule(isDebugEnabled: Boolean) = module {

    // Retrofit and OkHttp setup
    single { Json { ignoreUnknownKeys = true } }
    single { providesOkHttpClient(get(), isDebugEnabled) }
    single { providesRetrofit(get(), get()) }

    // API
    single { providesMarvelApi(get()) }
}

private fun providesMarvelApi(retrofit: Retrofit): MarvelApi =
    retrofit.create(MarvelApi::class.java)

private fun providesRetrofit(jsonSerializer: Json, okHttpClient: OkHttpClient) = Retrofit.Builder()
    .baseUrl(lbcBaseUrl)
    .client(okHttpClient)
    .addConverterFactory(jsonSerializer.asConverterFactory(mediaTypeJson.toMediaType()))
    .build()

private fun providesOkHttpClient(context: Context, isDebugEnabled: Boolean): OkHttpClient = OkHttpClient()
    .newBuilder()
    .cache(Cache(context.cacheDir, apiCacheSize))
    .addInterceptor(marvelAuthentInterceptor())
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (isDebugEnabled)
            Level.BODY
        else
            Level.NONE
    })
    .build()

private fun marvelAuthentInterceptor() = Interceptor { chain ->
    val originalRequest = chain.request()
    val originalUrl = originalRequest.url

    val ts = System.currentTimeMillis()
    val hash = "$ts${BuildConfig.PRIVATE_API_KEY}${BuildConfig.PUBLIC_API_KEY}".toMD5()

    val authenticatedUrl = originalUrl.newBuilder()
        .addQueryParameter("apikey", BuildConfig.PUBLIC_API_KEY)
        .addQueryParameter("ts", "$ts")
        .addQueryParameter("hash", hash)
        .build()

    // Request customization: add request headers
    val authenticatedRequest = originalRequest.newBuilder()
        .url(authenticatedUrl)
        .addHeader("Accept", "*/*")
        .build()

    return@Interceptor chain.proceed(authenticatedRequest)
}

private const val lbcBaseUrl = "https://gateway.marvel.com/v1/public/"
private const val mediaTypeJson = "application/json"
private const val apiCacheSize = 1024 * 1024 * 2L




