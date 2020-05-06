/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2016  Manuel Gonzales Jr.
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see [http://www.gnu.org/licenses/].
*
*/
package com.senior.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senior.javnav.R
import org.w3c.dom.Node
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class GoogleFragment : Fragment(), OnMapReadyCallback {
    lateinit private var map: View
    lateinit var TAMUK: GoogleMap
    private var TAMUKFrag: SupportMapFragment? = null

    //Initalized with TAMUK location.
    var currentLoc = LatLng(27.524285, -97.882433)
    private var buildingList: Spinner? = null
    private var buildingGetter: getBuildings? = null
    private val buildingNames = ArrayList<String?>()
    private val buildingCoord = ArrayList<String?>()
    private var currentMode = 0
    private var navigate = 0
    private var getBooleanCompleted = false
    override fun onCreateView(inflate: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("Google", "Oncreate view called")
        map = inflate.inflate(R.layout.google_fragment, container, false)
        buildingList = map.findViewById<View>(R.id.buildings) as Spinner
        buildingGetter = getBuildings()
        buildingNames.clear()
        buildingCoord.clear()

        //Adds and organizes the buildings alphabetically
        try {
            buildingNames.addAll(buildingGetter!!.execute().get())
            Collections.sort(buildingNames, java.lang.String.CASE_INSENSITIVE_ORDER)
            buildingCoord.addAll(buildingNames)
            Collections.sort(buildingCoord, java.lang.String.CASE_INSENSITIVE_ORDER)
            for (index in buildingNames.indices) {
                buildingNames[index] = buildingNames[index]!!.substring(0, buildingNames[index]!!.indexOf(','))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!getBooleanCompleted) {
            val error = Toast.makeText(requireActivity().applicationContext, "Error in acquiring building list, please try again", Toast.LENGTH_SHORT)
            error.show()
        }
        buildingNames.add(0, "Select one")
        val array = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, buildingNames)
        buildingList!!.adapter = array

        //Places point on building when building is selected
        buildingList!!.onItemSelectedListener = object : OnItemSelectedListener {
            private var lat = 0.0
            private var lon = 0.0
            private var latString: String? = null
            private var longString: String? = null
            override fun onItemSelected(arg0: AdapterView<*>?, arg1: View,
                                        arg2: Int, arg3: Long) {
                if (arg2 != 0) {
                    navigate = 0
                    latString = buildingCoord[arg2 - 1]
                    latString = latString!!.substring(latString!!.indexOf(',') + 1, latString!!.lastIndexOf(','))
                    longString = buildingCoord[arg2 - 1]
                    longString = longString!!.substring(longString!!.lastIndexOf(',') + 1)
                    lat = latString!!.toDouble()
                    lon = longString!!.toDouble()
                    TAMUK.clear()
                    TAMUK.addMarker(MarkerOptions().position(LatLng(lat, lon)).title(buildingNames[arg2]).snippet("Touch marker twice to start navigation"))
                    TAMUK.setOnMarkerClickListener {
                        navigate++
                        if (navigate == 2) {
                            navigate = 0
                            val shared = PreferenceManager.getDefaultSharedPreferences(activity)
                            val mode = shared.getString("navigationSelect", "d")
                            val avoid = shared.getStringSet("avoidSelect", null)
                            var uri: String? = "google.navigation:q=$lat,$lon&mode=$mode"
                            if (avoid != null) {
                                uri += "&avoid="
                                for (index in avoid.indices) {
                                    uri += avoid.toTypedArray()[index]
                                }
                            }
                            val googleURI = Uri.parse(uri)
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = googleURI
                            startActivity(intent)
                        }
                        false
                    }
                    val tempLatLng = LatLng(lat, lon)
                    currentLoc = tempLatLng
                    TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLatLng, 17f))
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }
        return map
    }

    override fun onStart() {
        super.onStart()
        TAMUKFrag = childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        TAMUKFrag!!.getMapAsync(this)
    }

    override fun onDestroyView() {
        Log.i("Google", "Destroy view called")
        super.onDestroyView()
        try {
            Log.i("Google", "Destroy executing")
            val frag: Fragment?
            frag = childFragmentManager.findFragmentById(R.id.google_map)

            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.remove(frag!!)
            ft.commit()
        } catch (e: Exception) {
            Log.i("Google", "Error in destroying map $e")
        }
        Log.i("Google", "On destroy complete!")
    }

    override fun onMapReady(map: GoogleMap) {
        TAMUK = map
        val shared = PreferenceManager.getDefaultSharedPreferences(activity)
        val mapSel = shared.getString("mapSelect", "4")
        TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16f))
        if (Build.VERSION.SDK_INT >= 23) {
            val coarseLocation = requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            val fineLocation = requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            val granted = PackageManager.PERMISSION_GRANTED
            if (coarseLocation != granted && fineLocation != granted) {
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                requireActivity().requestPermissions(permissions, 1)
            } else {
                TAMUK.isMyLocationEnabled = true
            }
        }

        //Sets the camera view as determined by the users settings
        currentMode = mapSel!!.toInt()
        TAMUK.mapType = currentMode
        val trafficEnabled = shared.getBoolean("trafficSelect", false)
        TAMUK.isTrafficEnabled = trafficEnabled
        Log.i("Google", "Map setting set")
    }

    private inner class getBuildings : AsyncTask<String?, Void?, ArrayList<String>>() {
        var buildingArray = ArrayList<String>()
        override fun doInBackground(vararg params: String?): ArrayList<String> {
            val buildingList = getString(R.string.buildingListUrl)
            try {
                val dbf = DocumentBuilderFactory.newInstance()
                val db = dbf.newDocumentBuilder()
                val url = URL(buildingList)
                val inputStream = url.openStream()
                val document = db.parse(inputStream)
                inputStream.close()
                val buildings = document.getElementsByTagName("building")
                var building: Node?
                for (index in 0 until buildings.length) {
                    building = buildings.item(index)
                    buildingArray.add(building.textContent)
                    Log.i("Google", "Building " + building.textContent)
                }
                getBooleanCompleted = true
            } catch (e: Exception) {
                e.printStackTrace()
                getBooleanCompleted = false
            }
            return buildingArray
        }
    }
}