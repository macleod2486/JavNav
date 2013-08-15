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


//JSoup imports
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//Android imports
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class NewsUpdate extends IntentService 
{

	String titles[] = new String[30];
	boolean firstRun=false;
	
	public NewsUpdate(String name) 
	{
		super(name);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) 
	{
		Update checkForNew = new Update();
		checkForNew.execute(1);
	}
	
	//Checks for any changes within the website
	private class Update extends AsyncTask<Integer, Void, Void>
	{
		int pointer = 0;
		boolean different;
		String Sync[]=new String[30];
		Toast notification = Toast.makeText(getApplicationContext(), "New", Toast.LENGTH_SHORT);
		@Override
		protected Void doInBackground(Integer...go)
		{
				
			try
			{
				while(!isCancelled())	
				{	
					Document document = Jsoup.connect("http://www.tamuk.edu").get();
					Elements newsDiv =document.select("div#newsbody");
					Elements newContent = newsDiv.select("div#newscontent");
					
					for(Element Division :newContent)
					{
						if(!titles[pointer].equals(Division.toString()))
							different=true;
						Sync[pointer]=Division.toString();
						pointer++;
					}
					
					if(different)
					{
						for(int start=0; start<pointer; start++)
						{
							titles[start]=Sync[start];
						}
						pointer=0;
						different=false;
						notification.show();
					}
					Thread.sleep(6000);
				}
			}
			catch(Exception e)
			{
				Log.i("Service","Error "+e);
			}			
			return null;
		}

			
	}
}
