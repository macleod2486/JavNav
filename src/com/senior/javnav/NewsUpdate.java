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
//Java Imports
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
//JSoup imports
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//Android imports
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
		checkNew.execute(1);
		
		Log.i("Service","It works!");
	}

	//Async task that checks for the update
	private class Update extends AsyncTask<Integer, Void, Void>
	{
		int pointer = 0;
		boolean different;
		String Sync[]=new String[30];
		
		//Two 
		Notification notifi;
		NotificationManager notifiManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		
		//Will read from the file that is created
		File filePath = getBaseContext().getCacheDir();
		String path = filePath.toString();
		File data = new File(path+"/news.txt");
		
		boolean updated = false;
		
		
		//Executes the following in the background
		@Override
		protected Void doInBackground(Integer...go)
		{
			Log.i("Service","Checking for updates");
			//checks to see if the temp file needs to be created
			boolean isFileEmpty;
			
			//Checks to see if the file is there
			isFileEmpty=isFileNull();
			
			Log.i("String","File path "+path);
			
			if(isFileEmpty)
			{
				Log.i("Service","File was empty");
				createFile();
			}
			else
			{
				Log.i("Service","File exists");
				getUpdate();
			}
		
			return null;
		}
		
		
		@SuppressWarnings("deprecation")
		
		//After the async task completes the execution 
		@Override
		protected void onPostExecute(Void go)
		{
			if(updated)
			{
				Intent homeIntent = new Intent(getBaseContext(),MainActivity.class);
				PendingIntent homePending = PendingIntent.getActivity(getBaseContext(), 0,homeIntent, 0);
				notifi= new Notification(R.drawable.ic_launcher,"JavNav",System.currentTimeMillis());
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
				Elements newsDiv =document.select("div#calendar");
				Elements newsLinks = newsDiv.select("a[href]");
				fw.write("");
				for(Element Division :newsLinks)
				{
					fw.append(Division.toString()+"\n");
					Log.i("Service","Found "+Division.toString());
				}
				fw.close();
				Log.i("Service","Completed creating file");
			}
			catch(Exception e)
			{
				Log.i("Service","Error "+e);
			}
		}
		
		//If the file is not empty then it will grab and check updates
		private void getUpdate()
		{
			Document document;
			Elements newsDiv;
			Elements newContent;
			Scanner updateScan;
			FileWriter fw;
			try
			{
				updateScan = new Scanner(data);
				
				document = Jsoup.connect("http://www.tamuk.edu/").get();
				newsDiv =document.select("div#calendar");
				newContent = newsDiv.select("a[href]");
				
				for(Element Division :newContent)
				{
					if(!updateScan.nextLine().contains(Division.toString()))
						different=true;
					Sync[pointer]=Division.toString();
					pointer++;
				}
				
				//Closes the scanner
				updateScan.close();
				
				//If there is a difference then the file is updated
				if(different)
				{
					fw=new FileWriter(data);
					fw.write("");
					for(int start=0; start<pointer; start++)
					{
						fw.append(Sync[start]+"\n");
					}
					
					//Closes file
					
					fw.close();
					Log.i("Service","New updates found!");
					updated=true;
				}
				else
				{
					Log.i("Service","No new updates");
				}
					
			}
			catch(Exception e)
			{
				Log.i("Service","Error "+e);
			}
		
		}
		
		//Checks to see if the file is new or is empty
		private boolean isFileNull()
		{
			boolean checkNull;
			try
			{	
				Scanner temp = new Scanner(data);
				Log.i("Service","Checking the file");
				
				checkNull=temp.hasNext();
				
				if(temp.hasNext())
					checkNull=false;
				else
					checkNull=true;
				temp.close();
			}
			catch(Exception e)
			{
				//e.printStackTrace();
				Log.i("Service","IsFileNull error "+e);
				checkNull=true;
			}
			
			return checkNull;
		}
			
	}

	
}
