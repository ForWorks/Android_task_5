package com.example.myapplication.api

import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants.Companion.BASE_URL
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    private val api by lazy {
        retrofit.create(APIService::class.java)
    }

    fun getATMs(): Observable<List<Item>> {
        return api.getATMs()
    }

    fun getBanks(): Observable<List<Item>> {
        return api.getBanks()
    }

    fun getKiosks(): Observable<List<Item>> {
        return api.getKiosks()
    }
}