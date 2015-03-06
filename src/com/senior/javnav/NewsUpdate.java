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
        JavSQL sql = new JavSQL(getBaseContext(), "JavSql", null, 1);

        boolean newLink = false;
		
		//Executes the following in the background
		@Override
		protected Void doInBackground(Void...go)
		{
			Log.i("JavService","Starting");
			
            try
            {
                int numberOfSaved = sql.getSaved();

                Log.i("JavService","Number of entries "+numberOfSaved);

                if(numberOfSaved == 0)
                {
                    fillTable();
                }

                else
                {
                    check();
                }

            }
            catch(Exception e)
            {
                Log.e("JavService","Error "+e);
            }
			
			return null;
		}
		
		@SuppressWarnings("deprecation")
		
		@Override
		protected void onPostExecute(Void go)
		{
			if(newLink)
			{
                Notification notifi;
                NotificationManager notifiManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

				Intent homeIntent = new Intent(getBaseContext(),MainActivity.class);
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				
				PendingIntent homePending = PendingIntent.getActivity(getBaseContext(), 0, homeIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
				
				notifi = new Notification(R.drawable.ic_notification,"JavNav",System.currentTimeMillis());
				notifi.setLatestEventInfo(getApplicationContext(), "JavNav", "New Events!", homePending);
				notifi.flags = Notification.FLAG_AUTO_CANCEL;
				notifiManage.notify(0,notifi);				
			}

            sql.closeDb();
            sql.close();
		}

        private void fillTable()
        {
            Log.i("JavService","Filling the table with the links");

            Document document;
            Elements newsDiv;
            Elements newsTitles;
            Elements newsLinks;

            try
            {
                document = Jsoup.connect("http://www.tamuk.edu/").get();
                newsDiv = document.select("div#calendar");
                newsTitles = newsDiv.select("a");
                newsLinks = newsTitles.select("[href]");

                for(int index = 0; index < newsLinks.size(); index++)
                {
                    sql.insertInTable(newsLinks.get(index).toString());
                }
            }
            catch (Exception e)
            {
                Log.e("JavService","Error "+e);
            }
        }

        //Checks for any changes
		private void check()
        {
            Document document;
            Elements newsDiv;
            Elements newsTitles;
            Elements newsLinks;

            Log.i("JavService","Checking for new links");

            try
            {
                document = Jsoup.connect("http://www.tamuk.edu/").get();
                newsDiv = document.select("div#calendar");
                newsTitles = newsDiv.select("a");
                newsLinks = newsTitles.select("[href]");

                boolean exists;

                //Removes any links in the database that no longer exist.
                for(int index = 0; index < newsLinks.size(); index++)
                {
                    sql.clearOld(newsLinks.get(index).toString());
                }

                //Inserts any links that are new.
                for(int index = 0; index < newsLinks.size(); index++)
                {
                    exists = sql.existInTable(newsLinks.get(index).toString());

                    if(exists)
                    {
                        Log.i("JavService","Exists in table");
                    }

                    else
                    {
                        sql.insertInTable(newsLinks.get(index).toString());
                        newLink = true;
                        Log.i("JavService","New link inserted");
                    }
                }
            }
            catch(Exception e)
            {
                Log.e("JavService","Error "+e);
            }
        }
	}
}
