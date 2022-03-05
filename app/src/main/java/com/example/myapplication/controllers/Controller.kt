package com.example.myapplication.controllers

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.Companion.ATM
import com.example.myapplication.utils.Constants.Companion.BANK
import com.example.myapplication.utils.Constants.Companion.KIOSK
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable.fromIterable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.pow
import kotlin.math.sqrt

class Controller {

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetwork != null)
            return true
        return false
    }

    fun addMarkers(point: LatLng, map: GoogleMap) {
        val retrofit = RetrofitInstance
        Single.zip(retrofit.getATMs(), retrofit.getKiosks(), retrofit.getBanks(),
            { atms, kiosks, banks ->
                val list = mutableListOf<Item>()
                fillLists(atms, kiosks, banks)
                list += atms + kiosks + banks
                list
            })
            .map { list ->
                list.sortedWith(
                    compareBy { sqrt((point.latitude - it.x).pow(2) + (point.longitude - it.y).pow(2)) })
            }
            .flatMapObservable { elements -> fromIterable(elements) }
            .take(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { value -> addMarker(value, map) },
                { error -> Log.e(Constants.ERROR, "$error") },
                { Log.e(Constants.SUCCESS, " ") }
            )
    }

    private fun fillLists(atms: List<Item>, kiosks: List<Item>, banks: List<Item>) {
        atms.forEach { it.type = 0 }
        kiosks.forEach { it.type = 1 }
        banks.forEach { it.type = 2 }
    }

    private fun addMarker(value: Item, map: GoogleMap) {
        val marker = MarkerOptions()
            .position(LatLng(value.x, value.y))
            .snippet(value.cityType + " " + value.city + " " + value.house)
        when(value.type) {
            0 -> {
                marker.icon(defaultMarker(HUE_ORANGE))
                marker.title(ATM)
            }
            1 -> {
                marker.icon(defaultMarker(HUE_GREEN))
                marker.title(KIOSK)
            }
            2 -> {
                marker.icon(defaultMarker(HUE_YELLOW))
                marker.title(BANK)
            }
        }
        map.addMarker(marker)
    }
}