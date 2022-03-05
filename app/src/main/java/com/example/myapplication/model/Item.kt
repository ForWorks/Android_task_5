package com.example.myapplication.model

import com.google.gson.annotations.SerializedName

data class Item (
    @SerializedName("gps_x", alternate = ["GPS_X"])
    val x: Double,
    @SerializedName("gps_y", alternate = ["GPS_Y"])
    val y: Double,
    @SerializedName("address_type", alternate = ["street_type"])
    val cityType: String,
    @SerializedName("address", alternate = ["street"])
    val city: String,
    @SerializedName("house", alternate = ["home_number"])
    val house: String
) {
    var type: Int = 0
}