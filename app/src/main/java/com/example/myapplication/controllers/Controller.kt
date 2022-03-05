package com.example.myapplication.controllers

import com.example.myapplication.model.Item
import com.example.myapplication.utils.Constants.Companion.ATM
import com.example.myapplication.utils.Constants.Companion.BANK
import com.example.myapplication.utils.Constants.Companion.KIOSK
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Controller {

    fun addMarker(value: Item, map: GoogleMap) {
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