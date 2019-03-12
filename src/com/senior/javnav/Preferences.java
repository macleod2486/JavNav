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

//This is where all the settings appear.

package com.senior.javnav;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		PreferenceManager.setDefaultValues(Preferences.this, R.xml.settings, false);
		initSummary(getPreferenceScreen());
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onStop()
	{
		Log.i("Preferences","On stop called.");

		//Start the service if enabled and it hasn't started
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

		if(shared.getBoolean("notifi", true))
		{
			long minutes = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS;

			PeriodicWorkRequest.Builder scheduledWorkRequestBuild = new PeriodicWorkRequest.Builder(NewsUpdate.class, minutes, TimeUnit.MINUTES);
			scheduledWorkRequestBuild.addTag("JavServiceUpdater");
			scheduledWorkRequestBuild.setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());
			PeriodicWorkRequest scheduledWorkRequest = scheduledWorkRequestBuild.build();
			WorkManager.getInstance().enqueueUniquePeriodicWork("JavServiceUpdater", ExistingPeriodicWorkPolicy.REPLACE, scheduledWorkRequest);

			Log.i("Preferences","Service started");
		}
		
		//Cancel the service if not enabled anymore
		if(!shared.getBoolean("notifi", true))
		{
			WorkManager.getInstance().cancelAllWorkByTag("JavServiceUpdater");
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

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
		updatePrefSummary(findPreference(key));
	}

	private void initSummary(Preference p)
	{
		if(p instanceof PreferenceGroup)
		{
			PreferenceGroup pGrp = (PreferenceGroup) p;
			for (int i = 0; i < pGrp.getPreferenceCount(); i++)
			{
				initSummary(pGrp.getPreference(i));
			}
		}

		else
		{
			updatePrefSummary(p);
		}
	}

	private void updatePrefSummary(Preference p)
	{
		if (p instanceof ListPreference)
		{
			ListPreference listPref = (ListPreference) p;
			p.setSummary(listPref.getEntry());
		}

		if(p instanceof EditTextPreference)
		{
			EditTextPreference editTextPref = (EditTextPreference) p;
			if (p.getTitle().toString().toLowerCase().contains("password"))
			{
				p.setSummary("******");
			}
			else
			{
				p.setSummary(editTextPref.getText());
			}
		}

		if(p instanceof MultiSelectListPreference)
		{
			MultiSelectListPreference editTextPref = (MultiSelectListPreference) p;
			SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
			Set<String> selections = shared.getStringSet(editTextPref.getKey(), null);

			if(selections != null)
			{
				String[] stringSelect = selections.toArray(new String[] {});
				String summary = "";

				for(int index = 0; index < stringSelect.length; index++)
				{
					switch(stringSelect[index])
					{
						case "t":
							summary += " Tolls ";
							break;
						case "f":
							summary += " Ferries ";
							break;
						case "h":
							summary += " Highways ";
							break;
						default:
							summary += stringSelect[index];
							break;
					}
				}

				p.setSummary(summary);
			}
		}
	}
}
