package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.api.RetrofitInstance
import com.example.myapplication.controllers.Controller
import com.example.myapplication.databinding.ActivityMapBinding
import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants.Companion.CENTER
import com.example.myapplication.utils.Constants.Companion.ERROR
import com.example.myapplication.utils.Constants.Companion.SUCCESS
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observable.fromIterable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.math.pow
import kotlin.math.sqrt

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private val controller by lazy { Controller() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val point = LatLng(52.425163, 31.015039)
        map.addMarker(MarkerOptions().position(point).title(CENTER))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 13f))

        val retrofit = RetrofitInstance
        Observable.zip(retrofit.getATMs(), retrofit.getKiosks(), retrofit.getBanks(),
            { atms, kiosks, banks ->
                val list = mutableListOf<Item>()
                atms.forEach { it.type = 0 }
                kiosks.forEach { it.type = 1 }
                banks.forEach { it.type = 2 }
                list.addAll(atms)
                list.addAll(kiosks)
                list.addAll(banks)
                list
            })
            .map { list -> list.sortedWith(
                compareBy { sqrt((point.latitude - it.x).pow(2) + (point.longitude - it.y).pow(2)) })
            }
            .flatMap { items -> fromIterable(items) }
            .take(10)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { value -> controller.addMarker(value, map) },
                { error -> Log.e(ERROR, "$error") },
                { Log.e(SUCCESS, " ") }
            )
    }
}