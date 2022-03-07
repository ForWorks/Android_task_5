package com.example.myapplication.controllers

import android.content.Context
import android.net.ConnectivityManager
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants.Companion.ATM
import com.example.myapplication.utils.Constants.Companion.BANK
import com.example.myapplication.utils.Constants.Companion.KIOSK
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
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

    fun getObservable(point: LatLng): Observable<Item> {
        val retrofit = RetrofitInstance
        val single = Single.zip(retrofit.getATMs(), retrofit.getKiosks(), retrofit.getBanks(),
            { atms, kiosks, banks ->
                fillLists(kiosks, banks)
                atms + kiosks + banks
            })
            .map { list -> list.sortedWith(
                compareBy { sqrt((point.latitude - it.x).pow(2) + (point.longitude - it.y).pow(2)) })
            }
            .flatMapObservable { fromIterable(it) }
            .distinct()
            .take(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
        return single
    }

    private fun fillLists(kiosks: List<Item>, banks: List<Item>) {
        kiosks.forEach { it.type = 1 }
        banks.forEach { it.type = 2 }
    }

    fun addMarkers(items: List<Item>, map: GoogleMap) {
        items.forEach {
            val marker = MarkerOptions()
                .position(LatLng(it.x, it.y))
                .snippet(it.cityType + " " + it.city + " " + it.house)
            when (it.type) {
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
}