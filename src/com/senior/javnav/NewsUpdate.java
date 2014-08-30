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

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class NewsUpdate extends IntentService 
{
	
	public NewsUpdate() 
	{
		super("NewsUpdate");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Update checkNew = new Update();
		checkNew.execute();
		
		Log.i("JavService","Started");
	}

	//Async task that checks for the update
	private class Update extends AsyncTask<Void, Void, Void>
	{
		boolean different;
		
		Notification notifi;
		NotificationManager notifiManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		File data = new File(getBaseContext().getCacheDir().toString()+"/news.txt");
		
		boolean updated = false;
		
		//Executes the following in the background
		@Override
		protected Void doInBackground(Void...go)
		{
			Log.i("JavService","Checking for updates");
			
			//checks to see if the temp file needs to be created
			boolean isFileEmpty;
			
			//Checks to see if the file is there
			isFileEmpty = isFileNull();
			
			if(!isFileEmpty)
			{
				Log.i("JavService","File was empty");
				createFile();
			}
			else
			{
				Log.i("JavService","File exists");
				getUpdate();
			}
		
			return null;
		}
		
		@SuppressWarnings("deprecation")
		
		@Override
		protected void onPostExecute(Void go)
		{
			if(updated)
			{
				Intent homeIntent = new Intent(getBaseContext(),MainActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				PendingIntent homePending = PendingIntent.getActivity(getBaseContext(), 0, homeIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
				
				notifi = new Notification(R.drawable.ic_notification,"JavNav",System.currentTimeMillis());
				notifi.setLatestEventInfo(getApplicationContext(), "JavNav", "New Events!", homePending);
				notifi.flags = Notification.FLAG_AUTO_CANCEL;
				notifiManage.notify(0,notifi);				
			}
		}
		
		//Creates the file if the file is found to be empty
		private void createFile()
		{
			try
			{
				FileWriter fw = new FileWriter(data);
				Document document = Jsoup.connect("http://www.tamuk.edu/").get();
				Elements newsDiv = document.select("div#calendar");
				Elements newsLinks = newsDiv.select("a[href]");
				
				fw.write("");
				fw.write(newsLinks.toString());
				fw.close();
				
				Log.i("JavService","Completed creating file");
			}
			catch(Exception e)
			{
				Log.i("JavService","Error "+e);
			}
		}
		
		//If the file is not empty then it will grab and check updates
		private void getUpdate()
		{
			Document document;
			Elements newsDiv;
			Elements newsLinks;
			Scanner updateScan;
			FileWriter fw;
			
			try
			{
				updateScan = new Scanner(data);
				updateScan.useDelimiter("\\A");
				
				document = Jsoup.connect("http://www.tamuk.edu/").get();
				newsDiv = document.select("div#calendar");
				newsLinks = newsDiv.select("a[href]");
				
				if(!newsLinks.toString().equals(updateScan.next()))
				{
					different = true;
				}
				
				//Closes the scanner
				updateScan.close();
				
				//If there is a difference then the file is updated
				if(different)
				{
					fw = new FileWriter(data);
					
					fw.write("");					
					fw.write(newsLinks.toString());
					fw.close();
					
					Log.i("JavService","New updates found!");
					
					updated = true;
				}
				else
				{
					Log.i("JavService","No new updates");
				}
					
			}
			catch(Exception e)
			{
				Log.i("JavService","Error "+e);
			}
		
		}
		
		//Checks to see if the file is new or is empty
		private boolean isFileNull()
		{
			boolean checkNull;
			
			try
			{	
				Scanner temp = new Scanner(data);
				
				checkNull = temp.hasNext();
				
				Log.i("JavService","Checking the file "+checkNull);
				
				temp.close();
			}
			catch(Exception e)
			{
				Log.i("JavService","IsFileNull error "+e);
				checkNull = false;
			}
			
			return checkNull;
		}
	}
}
