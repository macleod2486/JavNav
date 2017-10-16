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
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.senior.fragments.GoogleFragment;
import com.senior.fragments.HomeFragment;
import com.senior.fragments.WebViewFrag;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends ActionBarActivity
{		
	HomeFragment Home =  new HomeFragment();
	//Google map fragment
	GoogleFragment Google = new GoogleFragment();
	//Bluegold fragment
	WebViewFrag bluegold = new WebViewFrag();
	//Blackboard fragment
	WebViewFrag blackboard = new WebViewFrag();
	//Custom web fragment
	WebViewFrag custom = new WebViewFrag();
	
	String bluegoldurl = "https://www.tamuk.edu/bluegold";
	String blackboardurl = "https://blackboard.tamuk.edu";
	String customUrl = "";
	
	//Tabs that will be added to the actionbar
	ActionBar.Tab homeTab;
	ActionBar.Tab javTab;
	ActionBar.Tab blueTab;
	ActionBar.Tab blackTab;
	ActionBar.Tab customTab;
	
	//Manually handle the back button being pressed. 
	@Override
	public void onBackPressed()
	{
		Log.i("Main","Back button pressed");
		
		if(getSupportActionBar().getSelectedTab()!=homeTab)
		{
				super.onBackPressed();
		}
		else
		{
			Log.i("Main","Backstack count "+getSupportFragmentManager().getBackStackEntryCount());
			if(getSupportFragmentManager().getBackStackEntryCount()!=0 && !Home.isAdded())
			{
				getSupportFragmentManager().popBackStack();
			}
			else
			{
				getSupportActionBar().removeAllTabs();
				finish();
			}
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
		Fabric.with(this, new Crashlytics());
		
		//Checks to see if the device has internet access and alerts user if not
		if(!online())
		{
			AlertDialog.Builder connectBuild = new AlertDialog.Builder(this);
			
			connectBuild.setTitle("Warning!");
			connectBuild.setMessage("You need to be connected to the internet!").setCancelable(true);
			
			AlertDialog connect = connectBuild.create();
			connect.show();
		}
		
		//Seeds initial webpages.
		bluegold.loadUrl(bluegoldurl, true);
		blackboard.loadUrl(blackboardurl, true);
		
		//Sets up the actionbar
		ActionBar action = getSupportActionBar();
	
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		action.setDisplayUseLogoEnabled(false);
			
		homeTab = action.newTab();
		homeTab.setText("Home");
		homeTab.setTabListener(new ListTabListener(Home));
		action.addTab(homeTab);
		
		javTab = action.newTab();
		javTab.setText("Map");
		javTab.setTabListener(new TabListener(Google));
		action.addTab(javTab);
		
		blueTab = action.newTab();
		blueTab.setText("Bluegold");
		blueTab.setTabListener(new TabListener(bluegold));
		action.addTab(blueTab);
		
		blackTab = action.newTab();
		blackTab.setText("Blackboard");
		blackTab.setTabListener(new TabListener(blackboard));
		action.addTab(blackTab);

		//Sets the layout to the activity main layout
		setContentView(R.layout.activity_main);
	}
	
	public void onStart()
	{
		Log.i("Main","On start");
		super.onStart();
		
		ActionBar action = getSupportActionBar();
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);

		if(shared.getBoolean("secScreen",false))
		{
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
		}
		else
		{
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
		}

		//When the custom tab selection is toggled it makes the necessary changes
		if(shared.getBoolean("extra", false))
		{
			if(action.getTabCount()<5)
			{
				customTab = action.newTab();
				customTab.setText("Extra");
				customTab.setTabListener(new TabListener(custom));
				action.addTab(customTab);
				
				customUrl = shared.getString("webURL","http://www.google.com");
				custom.loadUrl(customUrl, true);
				
				Log.i("CustomUrl",customUrl);
			}
		}
		else
		{
			if(action.getTabCount()==5)
			{
				action.removeTabAt(4);
			}
		}
		
		//Loads new url if another one is entered
		if(action.getTabCount() == 5)
		{
			String tempurl = shared.getString("webURL","http://www.google.com");
			if(!tempurl.equals(customUrl))
			{
				customUrl = tempurl;
				custom.changeUrl(tempurl);
			}
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
		boolean alarmActive = (PendingIntent.getBroadcast(getApplicationContext(), 0,
				new Intent(getApplicationContext(),BroadcastNews.class),
				PendingIntent.FLAG_NO_CREATE) != null);

		//Start the service in a timely interval if not initialized
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		if(shared.getBoolean("notifi", true) && !alarmActive)
		{
			//one second * 60 seconds in a minute * minutes
			int interval = 1000*60*5;
			
			//Start the alarm manager service
			Intent service = new Intent(getApplicationContext(),BroadcastNews.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			//Set alarm to check for news
			newsUpdate.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), interval, pendingService);
			Log.i("Main","Alarm set "+shared.getBoolean("notifiCancelled", true));
		}
		
		super.onStop();
	}
	//When the item is selected from the settings list
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.i("Main","Setting option chosen");
		startActivity(new Intent(this,Preferences.class));
		return true;
	}
	
	//Creates the setting list
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Checks if the device is online
	private boolean online()
	{
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnectedOrConnecting();
	}
		
	/*
		Classes for each tab listener
	*/
	
	//Special class for the list fragment
	protected class ListTabListener extends FragmentActivity implements ActionBar.TabListener
	{
		public ListFragment fragment;
	   	 
		public ListTabListener(ListFragment fragment) 
		{
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) 
		{
			
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) 
		{
			try
			{
				Log.i("Tabs","Replaced called");
				ft.replace(R.id.container, fragment);
				
			}
			catch(Exception e)
			{
				Log.i("Main","Error in replacing fragment "+e);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) 
		{
			Log.i("Tabs","On remove called to "+fragment.toString());
			
			try
			{
				if(fragment.getId()!=R.id.google_map)
					ft.remove(fragment);
					
			}
			catch(Exception e)
			{
				Log.i("Main","Error in replacing frag "+e);
			}
		}
	}
	
	//Class for the fragments to be attached to the action bar
    protected class TabListener extends FragmentActivity implements ActionBar.TabListener
    {
   	 	public Fragment fragment;
   	 
		public TabListener(Fragment fragment) 
		{
			Log.i("Tabs","Fragment being reassigned");
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) 
		{
			
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) 
		{
			try
			{
				ft.replace(R.id.container, fragment);
			}
			catch(Exception e)
			{
				Log.i("Main","Error in replacing fragment "+e);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) 
		{

		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
	{
		switch(requestCode)
		{
			//Google
			case 01:
			{
				if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
				{
					Google.TAMUK.setMyLocationEnabled(true);
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
