package com.example.locations_ice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class restAdapter(context: Context, private val restaurants: List<String>) :
    ArrayAdapter<String>(context, 0, restaurants) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_main, parent, false)
        val restaurant = getItem(position)

        view.findViewById<TextView>(R.id.LSPlaces).text = restaurant

        return view
    }
}