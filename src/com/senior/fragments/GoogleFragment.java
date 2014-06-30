/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2014  Manuel Gonzales Jr.
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
package com.senior.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.senior.javnav.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class GoogleFragment extends Fragment 
{
	private View map;
	private GoogleMap TAMUK;
	
	public LatLng TAMUKLoc= new LatLng(27.524285,-97.882433);
	
	private Spinner buildingList;
	
	private ArrayList<String> buildingNames = new ArrayList<String>();
	private ArrayList<String> buildingCoord = new ArrayList<String>();
	
	private int currentMode;
	private int navigate;
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("Google","Oncreate view called");
		
		map = inflate.inflate(R.layout.google_fragment, container, false);
		buildingList = (Spinner)map.findViewById(R.id.buildings);
		
		buildingNames.clear();
		buildingCoord.clear();
		
		//Adds and organizes the buildings alphabetically
		buildingNames.addAll(Arrays.asList(getResources().getStringArray(R.array.listOfBuildings)));
		Collections.sort(buildingNames,String.CASE_INSENSITIVE_ORDER);
		
		buildingCoord.addAll(Arrays.asList(getResources().getStringArray(R.array.listOfBuildings)));
		Collections.sort(buildingCoord,String.CASE_INSENSITIVE_ORDER);
		
		for(int index = 0; index < buildingNames.size(); index++)
		{
			buildingNames.set(index, buildingNames.get(index).substring(0, buildingNames.get(index).indexOf(',')));
		}
		
		buildingNames.add(0,"Select one");
		
		ArrayAdapter<String> array = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,buildingNames);
		buildingList.setAdapter(array);
		
		//Places point on building when building is selected
		buildingList.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			private double lat = 0;
			private double lon = 0;
			private String latString;
			private String longString;
			
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				
				if(TAMUK != null && arg2 != 0)
				{
					navigate = 0;
					
					latString = buildingCoord.get(arg2-1);
					latString = latString.substring(latString.indexOf(',')+1,latString.lastIndexOf(','));
					
					longString = buildingCoord.get(arg2-1);
					longString = longString.substring(longString.lastIndexOf(',')+1);
					
					lat=Double.parseDouble(latString);
					lon=Double.parseDouble(longString);
					
					TAMUK.clear();
					TAMUK.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(buildingNames.get(arg2)));
					TAMUK.setOnMarkerClickListener(new OnMarkerClickListener()
					{
						@Override
						public boolean onMarkerClick(Marker marker) 
						{
							navigate++;
							
							if(navigate == 2)
							{
								navigate = 0;
								String url = "http://maps.google.com/maps?f=d&daddr="+lat+","+lon+"&dirflg=d";
								Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url)); 
								intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
								startActivity(intent);
							}
							return false;
						}
					});
					TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 17));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{ 
				
			}
			
		});
		
		setUpMap();
		
		return map;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		String mapSel = shared.getString("mapSelect", "4");
		
		if(this.currentMode != Integer.parseInt(mapSel))
		{
			this.currentMode = Integer.parseInt(mapSel);
			TAMUK.setMapType(currentMode);
		}
	}
	
	@Override
	public void onDestroyView()
	{
		Log.i("Google","Destroy view called");
		super.onDestroyView();
		
		try
		{
			Log.i("Google","Destroy executing");
			Fragment frag = (getFragmentManager().findFragmentById(R.id.google_map));
			FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
			ft.remove(frag);
			ft.commit();
			
		}
		catch(Exception e)
		{
			Log.i("Google","Error in destroying map "+e);
		}
		Log.i("Google","On destroy complete!");
	}
	
	//Sets up the map when loaded
	public void setUpMap()
	{
		Log.i("Google","oncreate called!");
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		String mapSel = shared.getString("mapSelect", "4");
		
		if(TAMUK==null)
		{
			TAMUK=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			Log.i("Google","Map recieved");
		}
		if(TAMUK!=null)
		{
			TAMUK=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			
			//Sets the options for the user to show their current location
			TAMUK.setMyLocationEnabled(true);
			
			TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(TAMUKLoc, 16));
			
			//Sets the camera view as determined by the users settings
			this.currentMode = Integer.parseInt(mapSel);
			TAMUK.setMapType(currentMode);
			
			Log.i("Google","Map setting set");
		}
		
	}
	
}
