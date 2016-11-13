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

//This is where all the settings appear.

package com.senior.javnav;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

public class Preferences extends PreferenceActivity
{
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	@Override
	public void onStop()
	{
		Log.i("Preferences","On stop called.");

		boolean alarmActive = (PendingIntent.getBroadcast(getApplicationContext(), 0,
				new Intent(getApplicationContext(),BroadcastNews.class),
				PendingIntent.FLAG_NO_CREATE) != null);

		//Start the service if enabled and it hasn't started
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		if(shared.getBoolean("notifi", true) && !alarmActive)
		{
			//one second * 60 seconds in a minute * 5
			int fiveMinutes = 1000*60*5;
			
			//Start the alarm manager service
			Intent service = new Intent(getApplicationContext(),BroadcastNews.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			//Check for the update every 5 minutes
			newsUpdate.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), fiveMinutes, pendingService);
			Log.i("Preferences","Service started");
		}
		
		//Cancel the service if not enabled anymore
		if(!shared.getBoolean("notifi", true) && alarmActive)
		{
			//Cancel the alarm manager service
			Intent service = new Intent(getApplicationContext(),BroadcastNews.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			newsUpdate.cancel(pendingService);
			pendingService.cancel();
			Log.i("Preferences","Service cancelled");
		}

		if(shared.getBoolean("secScreen",false))
		{
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		}
		else
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
		}
		
		super.onStop();
	}
}
