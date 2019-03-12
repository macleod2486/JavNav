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

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import io.fabric.sdk.android.Fabric;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.senior.fragments.GoogleFragment;
import com.senior.fragments.HomeFragment;
import com.senior.fragments.WebViewFrag;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{		
	HomeFragment Home =  new HomeFragment();
	//Google map fragment
	GoogleFragment Google = new GoogleFragment();
	//Bluegold fragment
	WebViewFrag bluegold = new WebViewFrag();
	//Blackboard fragment
	WebViewFrag blackboard = new WebViewFrag();

	DrawerLayout drawer;
	ActionBarDrawerToggle drawerToggle;
	int index = 0;
	
	String bluegoldurl = "https://www.tamuk.edu/bluegold";
	String blackboardurl = "https://blackboard.tamuk.edu";

	String title = "News";
	
	//Manually handle the back button being pressed. 
	@Override
	public void onBackPressed()
	{
		if(drawer.isDrawerOpen(GravityCompat.START))
		{
			Log.i("Main","Drawer closed");
			drawer.closeDrawers();
		}
		else if(index != 0 && !Home.isAdded())
		{
			getSupportFragmentManager().beginTransaction().replace(R.id.container, Home, "home").commit();
		}
		else
		{
			super.onBackPressed();
		}
	}
	
	/*
	 * Methods that are called to handle the activity lifecycle
	*/
	//When activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		super.onCreate(savedInstanceState);

		Crashlytics crashlyticsKit = new Crashlytics.Builder()
				.core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
				.build();
		Fabric.with(this, crashlyticsKit);

		//Sets the layout to the activity main layout
		setContentView(R.layout.activity_main);
		
		//Checks to see if the device has internet access and alerts user if not
		if(!online())
		{
			AlertDialog.Builder connectBuild = new AlertDialog.Builder(this);
			
			connectBuild.setTitle("Warning!");
			connectBuild.setMessage("You need to be connected to the internet!").setCancelable(true);
			
			AlertDialog connect = connectBuild.create();
			connect.show();
		}

		//Make the actionbar clickable to bring out the drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		
		//Seeds initial webpages.
		bluegold.loadUrl(bluegoldurl, true);
		blackboard.loadUrl(blackboardurl, true);

		//Configures the drawer
		drawer = findViewById(R.id.drawer);
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close)
		{
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(title);
				super.onDrawerClosed(view);
			}

			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(R.string.drawer_open);
				super.onDrawerOpened(drawerView);
			}
		};
		drawer.setDrawerListener(drawerToggle);
		drawer.setDrawerLockMode(drawer.LOCK_MODE_UNLOCKED);

		//Sets up the listview within the drawer
		String [] menuList = getResources().getStringArray(R.array.list);
		ListView list = (ListView)findViewById(R.id.optionList);
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuList));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View view, int position, long id)
			{
				Log.i("MainActivity","Position "+position);
				if(position == 0)
				{
					index = 0;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, Home, "home").commit();
					title = "News";
				}
				else if(position == 1)
				{
					index = 1;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, Google, "map").commit();
					title = "Map";
				}
				else if(position == 2)
				{
					index = 2;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, bluegold, "bluegold").commit();
					title = "Blue and Gold";
				}
				else if(position == 3)
				{
					index = 3;
					getSupportFragmentManager().beginTransaction().replace(R.id.container, blackboard, "blackboard").commit();
					title = "Blackboard";
				}
				else if(position == 4)
				{
					startActivity(new Intent(MainActivity.this, Preferences.class));
				}
				drawer.closeDrawers();
			}
		});

		//Displays the first fragment
		getSupportFragmentManager().beginTransaction().replace(R.id.container, Home, "home").commit();
		getSupportActionBar().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration config)
	{
		super.onConfigurationChanged(config);

		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			ActionBar action = getSupportActionBar();
			action.hide();
		}
		else if (config.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			ActionBar action = getSupportActionBar();
			action.show();
		}
	}
	
	//When the activity is destroyed
	@Override
	protected void onDestroy()
	{
		Log.i("Main","Destroy called");
		super.onDestroy();
	}
	
	@Override
	protected void onStop()
	{
		//Start the service
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		if(shared.getBoolean("notifi", true))
		{
			long minutes = PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS;

			PeriodicWorkRequest.Builder scheduledWorkRequestBuild = new PeriodicWorkRequest.Builder(NewsUpdate.class, minutes, TimeUnit.MINUTES);
			scheduledWorkRequestBuild.addTag("JavServiceUpdater");
			scheduledWorkRequestBuild.setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());
			PeriodicWorkRequest scheduledWorkRequest = scheduledWorkRequestBuild.build();
			WorkManager.getInstance().enqueueUniquePeriodicWork("JavServiceUpdater", ExistingPeriodicWorkPolicy.REPLACE, scheduledWorkRequest);

			Log.i("Main","Alarm set "+shared.getBoolean("notifiCancelled", true));
		}
		
		super.onStop();
	}
	//Toggles open the drawer
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawers();
		}
		else
		{
			drawer.openDrawer(GravityCompat.START);
		}
		return true;
	}
	
	//Checks if the device is online
	private boolean online()
	{
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnectedOrConnecting();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch(requestCode)
		{
			//Google
			case 01:
			{
				if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					if(ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
					{
						Google.TAMUK.setMyLocationEnabled(true);
					}
				}
				else
				{
					Toast.makeText(this, "Please allow location to be enabled to view yourself on the map",Toast.LENGTH_SHORT).show();
				}

				return;
			}
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
