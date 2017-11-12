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
package com.senior.javnav;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class BroadcastNews extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(arg0.getApplicationContext());

		Log.i("BroadcastNews", "Broadcast received");

		if(arg1.toString().contains(Intent.ACTION_BOOT_COMPLETED) && shared.getBoolean("notifi", false))
		{
			//one second * 60 seconds in a minute * minutes
			long interval = 1000*60*5;
			
			Intent service = new Intent(arg0, BroadcastNews.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(arg0, 0, service, 0);
			AlarmManager newsUpdate = (AlarmManager)arg0.getSystemService(arg0.ALARM_SERVICE);

			//Check for the update based on interval
			newsUpdate.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingService);
			
			Log.i("JavBroadcast","JavService started");
		}
		else
		{
			//Starting the news update class
			Intent newsUpdate = new Intent(arg0,NewsUpdate.class);
			JobIntentService.enqueueWork(arg0, NewsUpdate.class, 1000, newsUpdate);
			
			Log.i("JavBroadcast","Broadcast finished");
		} 
	}

}
