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
package com.senior.javnav;


//Actionbar sherlock imports
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
//Google imports
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//Fragment imports
import com.senior.fragments.BlackboardFrag;
import com.senior.fragments.BluegoldFragment;
import com.senior.fragments.CustomWebpage;
import com.senior.fragments.GoogleFragment;
import com.senior.fragments.HomeFragment;
//Android imports
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Spinner;

public class MainActivity extends SherlockFragmentActivity {
	
	public static final String ServiceName = "android.intent.action.service";
	
	SherlockListFragment Home =  new HomeFragment();
	//Google map module
	SherlockFragment Google = new GoogleFragment();
	//Bluegold module
	SherlockFragment bluegold = new BluegoldFragment();
	//Blackboard module
	SherlockFragment blackboard = new BlackboardFrag();
	//Custom web fragment
	SherlockFragment custom = new CustomWebpage();
	
	boolean goldtab=true;
	boolean boardtab = true;
	boolean maptab = true;
	boolean maintab = true;
	
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
				//finish();
				saveState();
				getSupportActionBar().removeAllTabs();
				super.onBackPressed();
		}
		else
		{
			Log.i("Main","Backstack count "+getSupportFragmentManager().getBackStackEntryCount());
			if(getSupportFragmentManager().getBackStackEntryCount()!=0)
			{
				getSupportFragmentManager().popBackStack();
			}
			else
			{
				//finish();	
				saveState();
				getSupportActionBar().removeAllTabs();
				super.onBackPressed();
			}
		}
		
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		Log.i("Main Activity","onConfigChanged");
		super.onConfigurationChanged(newConfig);
	}
	
	/*
	 * Methods that are called to handle the activity lifecycle
	*/
	//When activity is created
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{	
		

		super.onCreate(savedInstanceState);
		//Add new customizable features
		Log.i("Main","Main activity called!");
		
		//Checks to see if the device is online and alerts user if not
		if(!online())
		{
			AlertDialog.Builder connectBuild = new AlertDialog.Builder(this);
			
			connectBuild.setTitle("Warning!");
			connectBuild.setMessage("You need to be connected to the internet!").setCancelable(true);
			
			AlertDialog connect = connectBuild.create();
			connect.show();
		}
		
		Log.i("Main","Maintab entered");
		
		//Sets up the actionbar
		ActionBar action = getSupportActionBar();
	
		action.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		action.setDisplayShowTitleEnabled(false);
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
			
		Log.i("Main","On start called");
		super.onStart();
		
		ActionBar action = getSupportActionBar();
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		
		Log.i("Main","Current value "+shared.getBoolean("extra", false));
		
		//When the custom tab selection is toggled it makes the necessary changes
		if(shared.getBoolean("extra", false))
		{
			if(action.getTabCount()<5)
			{
				customTab = action.newTab();
				customTab.setText("Extra");
				customTab.setTabListener(new TabListener(custom));
				action.addTab(customTab);
			}
		}
		else
		{
			if(action.getTabCount()==5)
			{
				action.removeTabAt(4);
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
		//Start the service in a timely interval
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		if(shared.getBoolean("notifi", false)&&shared.getBoolean("notifiCancelled", true))
		{
			//one second * 60 seconds in a minute * 5
			int fiveMinutes = 1000*60*5;
			
			//Start the alarm manager service
			SharedPreferences.Editor edit = shared.edit();
			Intent service = new Intent(getApplicationContext(),BroadcastNews.class);
			PendingIntent pendingService = PendingIntent.getBroadcast(getApplicationContext(),0,service,0);
			AlarmManager newsUpdate = (AlarmManager)getSystemService(ALARM_SERVICE);
			
			//Check for the update every 5 minutes
			newsUpdate.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), fiveMinutes, pendingService);
			edit.putBoolean("notifiCancelled", false).commit();
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
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/*
	 	*
	 	*
	 	*
	 	*Various methods that are called on by the specified fragments.
	 	*
	 	*
	 	*
	 	*
	*/
	
	//Checks if the device is online
	public boolean online()
	{
		ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if(info !=null && info.isConnectedOrConnecting())
		{
			return true;
		}
		else
			return false;
	}
	
	//Saves the state of the fragment
	private void saveState()
	{
		int position = getSupportActionBar().getSelectedTab().getPosition();
		
		if(position==0)
		{	
			Home.setRetainInstance(true);
		}
		else if(position==1)
		{	
			Google.setRetainInstance(true);
		}
		else if(position==2)
		{	
			bluegold.setRetainInstance(true);
		}
		else if(position==3)
		{
			blackboard.setRetainInstance(true);
		}
		else if(position==4)
		{
			custom.setRetainInstance(true);
		}
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor edit = shared.edit();
		edit.putInt("TabSelect", position).commit();
		
	}
	
	//Handles the webviews buttons
	public void website(View view)
	{
		if(view.getId()==R.id.backBlack)
		{
			WebView blackboard = (WebView)findViewById(R.id.blackboardView);
			if(blackboard.canGoBack())
				blackboard.goBack();
		}
		else if(view.getId()==R.id.refreshBlack)
		{
			WebView blackboard = (WebView)findViewById(R.id.blackboardView);	
			blackboard.reload();
		}
		else if(view.getId()==R.id.blackForward)
		{
			WebView blackboard = (WebView)findViewById(R.id.blackboardView);
			if(blackboard.canGoForward())	
				blackboard.goForward();
		}
		else if(view.getId()==R.id.blueBack)
		{
			WebView bluegold = (WebView)findViewById(R.id.webBluegold);
			if(bluegold.canGoBack())
				bluegold.goBack();
		}
		else if(view.getId()==R.id.blueRefresh)
		{
			WebView bluegold = (WebView)findViewById(R.id.webBluegold);
			bluegold.reload();
		}
		else if(view.getId()==R.id.blueForward)
		{	WebView bluegold = (WebView)findViewById(R.id.webBluegold);
			if(bluegold.canGoForward())
				bluegold.goForward();
		}
		else if(view.getId()==R.id.custBack)
		{
			WebView custView = (WebView)findViewById(R.id.custom);
			if(custView.canGoBack())
				custView.goBack();
		}
		else if(view.getId()==R.id.custRefr)
		{
			WebView custView = (WebView)findViewById(R.id.custom);
			custView.reload();
		}
		else if(view.getId()==R.id.custForw)
		{
			WebView custView = (WebView)findViewById(R.id.custom);
			if(custView.canGoForward())
			{
				custView.goForward();
			}
		}
	}
	/*
		
		This method is called when the find button is selected in the map fragment
	*/
	public void find(View view)
	{
		Coordinates coorTAM = new Coordinates();
		double lat=0;
		double lon=0;
		Spinner building = (Spinner)findViewById(R.id.buildings);
		String selected=building.getSelectedItem().toString();
		GoogleMap TAMUK=((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.google_map)).getMap();
		
		if(TAMUK!=null&&building!=null)
		{
			
			selected = building.getSelectedItem().toString();
			TAMUK.clear();selected = building.getSelectedItem().toString();
			lat=coorTAM.latitude(selected);
			lon=coorTAM.longitude(selected);
			TAMUK.addMarker(new MarkerOptions().position(new LatLng(lat,lon)).title(selected));
			TAMUK.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon), 17));
		}
		
		Log.i("Main","Search is called "+selected+" Long="+lon+" "+"Latitude="+lat);
	}
	
	/*
		Classes for each tab listener
	*/
	
	//Special class for the list fragment
	protected class ListTabListener extends SherlockFragmentActivity implements ActionBar.TabListener
	{
		public SherlockListFragment fragment;
	   	 
		public ListTabListener(SherlockListFragment fragment) {
			// TODO Auto-generated constructor stub
			Log.i("Tabs","Fragment being reassigned");
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			Log.i("Tabs","Reselected");
			
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			try{
			Log.i("Tabs","Replaced called");
				ft.replace(R.id.container, fragment);
			}
			catch(Exception e)
			{
				Log.i("Main","Error in replacing fragment"+e);
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			// TODO Auto-generated method stub
			
			Log.i("Tabs","On remove called to "+fragment.toString());
			
			try
			{
				if(fragment.getId()!=R.id.google_map)
					ft.remove(fragment);
			}
			catch(Exception e)
			{
				Log.i("Main","Error in replacing frag"+e);
			}
			
		}
	}
	
	
//Class for the fragments to be attached to the action bar
    protected class TabListener extends SherlockFragmentActivity implements ActionBar.TabListener
    {
    	
   	 public SherlockFragment fragment;
   	 
		public TabListener(SherlockFragment fragment) 
		{
			
			Log.i("Tabs","Fragment being reassigned");
			this.fragment = fragment;
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) 
		{
			
			Log.i("Tabs","Reselected");
			
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
				Log.i("Main","Error in replacing fragment"+e);
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
				Log.i("Main","Error in replacing frag"+e);
			}
			
		}


	}
}
