package com.example.td2.network

import android.annotation.SuppressLint
import android.content.Context
import android.preference.PreferenceManager
import com.example.td2.SHARED_PREF_TOKEN_KEY
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Api(private val context: Context) {
    companion object {
        private const val BASE_URL = "https://android-tasks-api.herokuapp.com/api/"
//        private const val TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxOSwiZXhwIjoxNjA2OTAzODQwfQ.dKekGOToaJOOmqw6J18oMwA1PTwch2K0ovnig9hpkz8"
        @SuppressLint("StaticFieldLeak")
        lateinit var INSTANCE: Api
    }
    private val moshi = Moshi.Builder().build()

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer "+getToken())
                    .build()
                chain.proceed(newRequest)
            }
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val userService: UserService by lazy { retrofit.create(UserService::class.java) }
    val tasksService: TasksService by lazy { retrofit.create(TasksService::class.java) }

    private fun getToken() : String? {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SHARED_PREF_TOKEN_KEY, "")
    }
}