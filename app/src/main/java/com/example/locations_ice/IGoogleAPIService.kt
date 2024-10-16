package com.example.locations_ice

import com.example.locations_ice.MyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIService {
    @GET("maps/api/place/nearbysearch/json")
    fun getNearbyPlaces(@Url url: String): Call<MyPlaces>
}