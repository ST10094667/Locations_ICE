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
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale
import java.util.concurrent.Executors
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private var locationPermissionCode = 1
    private lateinit var txtlocation: TextView



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

        btnGps.setOnClickListener{
            getLocation()
            //getNearbyPlaces()
        }
    }
    private fun getLocation()
    {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if((ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ){
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode
            )
        }else
        {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, 5f, this
            )
        }
    }
    override fun onLocationChanged(location: Location) {
        /*txtlocation = findViewById(R.id.txtOutput)
        txtlocation.text = "Latitude: " + location.latitude + ", \nLongitude: " + location.longitude*/
        val latitude = location.latitude
        val longitude = location.longitude
        getAddressFromLocation(location)
        getNearbyPlaces(latitude, longitude)
    }
    private fun getAddressFromLocation(location: Location)
    {
        val geocoder = Geocoder(this, Locale.getDefault())
        txtlocation = findViewById(R.id.txtOutput)
        try{
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if(addresses != null && addresses.isNotEmpty()){
                val address = addresses[0]
                val addressLine = address.getAddressLine(0)
                txtlocation.text = "Address: $addressLine"
            }else
            {
                txtlocation.text = "Unable to get address"
            }
        }
        catch (e: IOException)
        {
            e.printStackTrace()
            txtlocation.text = "Error getting address"
        }
    }
    fun getNearbyPlaces(latitude: Double, longitude: Double) {
        val executor = Executors.newSingleThreadScheduledExecutor()
        executor.execute {
            try {
                val apiKey = "AIzaSyCBQMtPiU7Jilayud7o1R9cO8niMIn-2-s"
                val url = URL("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$latitude,$longitude&radius=500&type=restaurant&key=$apiKey")
                val connection = url.openConnection() as HttpURLConnection
                connection.setRequestProperty("Accept", "application/json")

                val json = connection.inputStream.bufferedReader().readText()
                val location = Gson().fromJson(json, LocationDetailsList::class.java)
                val output = location.search.map { it.places }

                Handler(Looper.getMainLooper()).post {
                    val listView = findViewById<ListView>(R.id.LSPlaces)

                    val adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        output
                    )
                    listView.adapter = adapter
                }
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    val textView = findViewById<TextView>(R.id.txtOutput)
                    textView.text = "Error: " + e.toString()
                }
            }
        }
    }
}