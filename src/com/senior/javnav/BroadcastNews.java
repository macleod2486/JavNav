/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2019  Manuel Gonzales Jr.
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.util.Log;

public class BroadcastNews extends BroadcastReceiver 
{
	@Override
	public void onReceive(Context arg0, Intent arg1) 
	{
		JavServiceScheduler scheduler = new JavServiceScheduler();
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(arg0.getApplicationContext());

		Log.i("BroadcastNews", "Broadcast received");

		if(arg1.toString().contains(Intent.ACTION_BOOT_COMPLETED) && shared.getBoolean("notifi", false))
		{
			String multiplier = shared.getString("notifInterval","1");
			scheduler.schedule(multiplier);

			Log.i("JavBroadcast","JavService started");
		}
		else
		{
			String multiplier = shared.getString("notifInterval","1");
			scheduler.schedule(multiplier);

			Log.i("JavBroadcast","Broadcast finished");
		} 
	}

}
