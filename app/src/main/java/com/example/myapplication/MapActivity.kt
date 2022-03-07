package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.controllers.Controller
import com.example.myapplication.databinding.ActivityMapBinding
import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.Constants.Companion.CENTER
import com.example.myapplication.utils.Constants.Companion.CHECK_CONNECTION
import com.example.myapplication.utils.Constants.Companion.COUNT
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val binding by lazy { ActivityMapBinding.inflate(layoutInflater) }
    private val info by lazy { Snackbar.make(binding.root, CHECK_CONNECTION, Snackbar.LENGTH_INDEFINITE) }
    private val controller by lazy { Controller() }
    private val handler by lazy { Handler(Looper.getMainLooper()) }
    companion object {
        private val items by lazy { mutableListOf<Item>() }
        private val point = LatLng(52.425163, 31.015039)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.addMarker(MarkerOptions().position(point).title(CENTER))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14f))

        if(items.size == COUNT)
            controller.addMarkers(items, map)
    }

    private val showInfo = object : Runnable {
        override fun run() {
            if (controller.isOnline(applicationContext)) {
                if(items.size != COUNT) {
                    items.clear()
                    controller.getObservable(point)
                        .subscribe(
                            { value -> items.add(value) },
                            { error -> Log.e(Constants.ERROR, "$error") },
                            { controller.addMarkers(items, map) })
                }
                if(info.isShown)
                    info.dismiss()
            }
            else
                if(!info.isShown)
                    info.show()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onStart() {
        super.onStart()
        handler.post(showInfo)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(showInfo)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(showInfo)
    }
}