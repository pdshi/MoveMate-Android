package com.stevennt.movemate.data.network

import android.content.Context
import com.stevennt.movemate.BuildConfig
import com.stevennt.movemate.BuildConfig.BASE_URL
import com.stevennt.movemate.Injection.provideUserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object APIConfig {

    private val baseURL: String = BuildConfig.BASE_URL

    private lateinit var authToken: String

    fun getApiService(context: Context) : APIService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            /*.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $authToken") // Add the authorization header with the authToken
                    .build()
                chain.proceed(request)
            }*/
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit.create(APIService::class.java)
    }

   /* var BASE_URL_MOCK: String? = null

    private const val HEADER_ACCEPT = "Accept"
    private const val HEADER_AUTHORIZATION = "Authorization"
    private const val MEDIA_TYPE_JSON = "application/json"

    fun getApiService(context: Context): APIService {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            )
        val interceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            runBlocking {
                val token = provideUserPreferences(context).getUserSession().first().token
                if (token != "") {
                    val request = originalRequest.newBuilder()
                        .header(HEADER_ACCEPT, MEDIA_TYPE_JSON)
                        .header(HEADER_AUTHORIZATION, "$token")
                        .method(originalRequest.method, originalRequest.body)
                        .build()
                    chain.proceed(request)
                } else {
                    chain.proceed(originalRequest)
                }
            }

        }
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_MOCK ?: BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(interceptor)
                    .build()
            )
            .build()
        return retrofit.create(APIService::class.java)
    }*/
}