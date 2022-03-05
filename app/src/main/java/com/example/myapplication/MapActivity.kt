package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.controllers.Controller
import com.example.myapplication.databinding.ActivityMapBinding
import com.example.myapplication.utils.Constants.Companion.CENTER
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
        controller.addMarkers(map, point)
    }
}