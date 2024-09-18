package com.example.locations_ice

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.Gson
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var locationPermissionCode = 1
    private lateinit var txtlocation: TextView
    private lateinit var txtName: TextView
    // private var latitude: Double = 0.0
    // private var longitude: Double = 0.0
    private lateinit var placesClient: PlacesClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btnGps: Button = findViewById(R.id.btnLocation)
        Places.initialize(applicationContext, "AIzaSyDzE7aySfu7ktU9xvtW03gfR5L_I7Im_sI")
        placesClient = Places.createClient(this)

        btnGps.setOnClickListener {
            getLocation()
            searchRestaurants()
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 5f, this
            )
        }
    }

    override fun onLocationChanged(location: Location) {
        /*txtlocation = findViewById(R.id.txtOutput)
        txtlocation.text = "Latitude: " + location.latitude + ", \nLongitude: " + location.longitude*/
        /*val latitude = location.latitude
        val longitude = location.longitude
        getAddressFromLocation(location)*/


        txtlocation = findViewById(R.id.txtOutput)
        val latitude = location.latitude
        val longitude = location.longitude
        txtlocation.text = "Latitude: $latitude, \nLongitude: $longitude"
        //getNearbyPlaces()

    }

    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())
        txtlocation = findViewById(R.id.txtOutput)
        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                txtlocation.text = "Address: $addressLine"
            } else {
                txtlocation.text = "Unable to get address"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            txtlocation.text = "Error getting address"
        }
    }

    fun displayRestaurants(restaurantNames: List<String>) {
        val listView: ListView = findViewById(R.id.LSPlaces)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            restaurantNames
        )
        listView.adapter = adapter
    }

    fun searchRestaurants() {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery("restaurants near me")
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val restaurantNames =
                response.autocompletePredictions.map { it.getPrimaryText(null).toString() }
            displayRestaurants(restaurantNames)
        }.addOnFailureListener { exception ->
            Log.e("MainActivity", "Place not found: ${exception.message}")
        }

    }
}