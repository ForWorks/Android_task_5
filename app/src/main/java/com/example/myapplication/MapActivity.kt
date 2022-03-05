package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.controllers.Controller
import com.example.myapplication.databinding.ActivityMapBinding
import com.example.myapplication.utils.Constants.Companion.CENTER
import com.example.myapplication.utils.Constants.Companion.CHECK_CONNECTION
import com.example.myapplication.utils.Constants.Companion.CLOSE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15f))

        if(!controller.isOnline(this))
            Snackbar.make(binding.root, CHECK_CONNECTION, Snackbar.LENGTH_LONG)
                .setAction(CLOSE) {}
                .show()

        CoroutineScope(Dispatchers.IO).launch {
            while (!controller.isOnline(applicationContext))
                continue
            controller.addMarkers(point, map)
        }
    }
}