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
package com.senior.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.senior.javnav.R;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GoogleFragment extends Fragment implements OnMapReadyCallback
{
	private View map;
	public GoogleMap TAMUK;
	private SupportMapFragment TAMUKFrag;

	//Initalized with TAMUK location.
	public LatLng currentLoc = new LatLng(27.524285,-97.882433);

	private Spinner buildingList;

	private getBuildings buildingGetter;
	
	private ArrayList<String> buildingNames = new ArrayList<String>();
	private ArrayList<String> buildingCoord = new ArrayList<String>();
	
	private int currentMode;
	private int navigate;

	private boolean getBooleanCompleted = false;
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("Google","Oncreate view called");
		
		map = inflate.inflate(R.layout.google_fragment, container, false);
		buildingList = (Spinner)map.findViewById(R.id.buildings);

		buildingGetter = new getBuildings();
		
		buildingNames.clear();
		buildingCoord.clear();
		
		//Adds and organizes the buildings alphabetically
		try
		{
			buildingNames.addAll(buildingGetter.execute().get());
			Collections.sort(buildingNames,String.CASE_INSENSITIVE_ORDER);

			buildingCoord.addAll(buildingNames);
			Collections.sort(buildingCoord,String.CASE_INSENSITIVE_ORDER);

			for(int index = 0; index < buildingNames.size(); index++)
			{
				buildingNames.set(index, buildingNames.get(index).substring(0, buildingNames.get(index).indexOf(',')));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		if(!getBooleanCompleted)
		{
			Toast error = Toast.makeText(getActivity().getApplicationContext(),"Error in acquiring building list, please try again", Toast.LENGTH_SHORT);
			error.show();
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
					TAMUK.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(buildingNames.get(arg2)).snippet("Touch marker twice to start navigation"));
					TAMUK.setOnMarkerClickListener(new OnMarkerClickListener()
					{
						@Override
						public boolean onMarkerClick(Marker marker) 
						{
							navigate++;
							
							if(navigate == 2)
							{
								navigate = 0;

                                SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());

                                String mode = shared.getString("navigationSelect","d");
								Set<String> avoid = shared.getStringSet("avoidSelect", null);
                                String uri = "google.navigation:q="+lat+","+lon+"&mode="+mode;

								if(avoid != null)
								{
									uri += "&avoid=";
									for(int index = 0; index < avoid.size(); index++)
									{
										uri += avoid.toArray()[index];
									}
								}

								Uri googleURI = Uri.parse(uri);
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(googleURI);
								startActivity(intent);
							}
							return false;
						}
					});
					LatLng tempLatLng = new LatLng(lat,lon);
					currentLoc = tempLatLng;
					TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(tempLatLng, 17));
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{ 
				
			}
			
		});
		
		return map;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		if (Build.VERSION.SDK_INT < 21)
		{
			TAMUKFrag = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.google_map);
		}
		else
		{
			TAMUKFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
		}

		TAMUKFrag.getMapAsync(this);
	}
	
	@Override
	public void onDestroyView()
	{
		Log.i("Google","Destroy view called");
		super.onDestroyView();
		
		try
		{
			Log.i("Google","Destroy executing");
			Fragment frag;
			if (Build.VERSION.SDK_INT < 21)
			{
				frag = (getFragmentManager().findFragmentById(R.id.google_map));
			}
			else
			{
				frag = getChildFragmentManager().findFragmentById(R.id.google_map);
			}
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

	@Override
	public void onMapReady(GoogleMap map)
	{
		TAMUK = map;

		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());

		String mapSel = shared.getString("mapSelect", "4");

		TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 16));

		if(Build.VERSION.SDK_INT >= 23)
		{
			int coarseLocation = getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
			int fineLocation = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
			int granted = PackageManager.PERMISSION_GRANTED;

			if (coarseLocation != granted && fineLocation != granted)
			{
				String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
				getActivity().requestPermissions(permissions, 01);
			}
			else
			{
				TAMUK.setMyLocationEnabled(true);
			}
		}

		//Sets the camera view as determined by the users settings
		this.currentMode = Integer.parseInt(mapSel);
		TAMUK.setMapType(currentMode);

		boolean trafficEnabled = shared.getBoolean("trafficSelect", false);
		TAMUK.setTrafficEnabled(trafficEnabled);

		Log.i("Google", "Map setting set");
	}

	private class getBuildings extends AsyncTask<String, Void, ArrayList<String>>
	{
		ArrayList<String> buildingArray= new ArrayList<String>();

		protected ArrayList<String> doInBackground(String...params)
		{
			String buildingList = getString(R.string.buildingListUrl);

			try
			{
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				URL url = new URL(buildingList);
				InputStream inputStream = url.openStream();
				Document document = db.parse(inputStream);
				inputStream.close();

				NodeList buildings = document.getElementsByTagName("building");
				Node building = null;

				for(int index = 0; index < buildings.getLength(); index++)
				{
					building = buildings.item(index);
					buildingArray.add(building.getTextContent());

					Log.i("Google","Building "+building.getTextContent());
				}

				getBooleanCompleted = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				getBooleanCompleted = false;
			}

			return buildingArray;
		}

	}
}
