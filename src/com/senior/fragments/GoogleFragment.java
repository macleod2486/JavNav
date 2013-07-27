/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2013  Manuel Gonzales Jr.
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

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.senior.javnav.R;


import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GoogleFragment extends SherlockFragment 
{
	View map;
	GoogleMap TAMUK;
	boolean backButton = false;
	public LatLng TAMUKLoc= new LatLng(27.524285,-97.882433);
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("Google","Oncreate view called");
		try
		{
		map=inflate.inflate(R.layout.google_fragment, container, false);
		setUpMap();
		}
		catch(Exception e)
		{
			Log.i("Google","Error on create "+e);
		}
		return map;
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
	
	@Override 
	public void onDetach()
	{
		super.onDetach();
		Log.i("Google","Detach called");
	}
	@Override
	public void onStart()
	{
		Log.i("Google","On start called");
		setUpMap();
		super.onStart();
	}
	@Override
	public void onResume()
	{
		Log.i("Google","On resume called");
		setUpMap();
		super.onResume();
	}
	public void onAttach()
	{
		Log.i("Google", "On attach called");
	}
	
	//Sets up the map when loaded
	public void setUpMap()
	{
		Log.i("Google","oncreate called!");
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		String mapSel = shared.getString("mapSelect", "1");
		
		//String mapSel="1";
		if(TAMUK==null)
		{
			TAMUK=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			Log.i("Google","Map recieved");
		}
		if(TAMUK!=null)
		{
			
			TAMUK=((SupportMapFragment)getFragmentManager().findFragmentById(R.id.google_map)).getMap();
			TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(TAMUKLoc, 16));
			
			if(mapSel.contains("1"))
			{	
				TAMUK.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}
			else if(mapSel.contains("2"))
			{	TAMUK.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
			else if(mapSel.contains("3"))
			{	
				TAMUK.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}
			
			Log.i("Google","Map setting set");
		}
		
		
		
	}
	
	

}
