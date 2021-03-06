package com.example.myapplication.api

import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants.Companion.ATM_URL
import com.example.myapplication.utils.Constants.Companion.BANK_URL
import com.example.myapplication.utils.Constants.Companion.KIOSK_URL
import io.reactivex.rxjava3.core.Single

import retrofit2.http.GET

interface APIService {
    @GET(ATM_URL)
    fun getATMs(): Single<List<Item>>

    @GET(KIOSK_URL)
    fun getKiosks(): Single<List<Item>>

    @GET(BANK_URL)
    fun getBanks(): Single<List<Item>>
}