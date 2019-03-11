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

import androidx.core.app.JobIntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class NewsUpdate extends JobIntentService
{
    JavSQL sql;

    boolean newLink = false;

    static void enqueueWork(Context context, Intent work)
    {
        enqueueWork(context, NewsUpdate.class, 1000, work);
    }

    @Override
    protected void onHandleWork(Intent intent)
    {
        sql = new JavSQL(this.getApplicationContext(), "JavSql", null, 1);

        Log.i("JavService", "Starting");

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

        if(newLink)
        {
            NotificationManager notifiManage = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder buildNotification;
            NotificationCompat.InboxStyle notificationStyle;
            PendingIntent homePending;

            Intent homeIntent = new Intent(this, MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(homeIntent);

            homePending = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationStyle = new NotificationCompat.InboxStyle();
            notificationStyle.setBigContentTitle("Events received");

            ArrayList<String> listOfLinks = sql.newEventsList();

            for(int index = 0; index < listOfLinks.size(); index++)
            {
                notificationStyle.addLine(listOfLinks.get(index));
            }

            buildNotification = new NotificationCompat.Builder(getBaseContext());
            buildNotification.setSmallIcon(R.drawable.ic_notification);
            buildNotification.setContentTitle("JavNav");
            buildNotification.setContentText("New Events!");
            buildNotification.setContentIntent(homePending);
            buildNotification.setAutoCancel(true);
            buildNotification.setStyle(notificationStyle);

            notifiManage.notify(0, buildNotification.build());
        }

        sql.closeDb();
        sql.close();


        Log.i("JavService","Finished");
    }

    private void fillTable()
    {
        Log.i("JavService","Filling the table with the links");

        Document document;
        Elements eventDiv;
        Elements eventTitles;
        Elements eventLinks;
        Elements newsDiv;
        Elements newsTitles;
        Elements newsLinks;

        try
        {
            document = Jsoup.connect("http://www.tamuk.edu/").get();

            eventDiv = document.select("div#calendar");
            eventTitles = eventDiv.select("a");
            eventLinks = eventTitles.select("[href]");

            newsDiv = document.select("div#inthenews");
            newsTitles = newsDiv.select("a");
            newsLinks = newsTitles.select("[href]");

            for(int index = 0; index < eventLinks.size() && index < eventTitles.size(); index++)
            {
                sql.insertInTable(eventLinks.get(index).attr("abs:href"),eventTitles.get(index).text());
            }

            for(int index = 0; index < newsLinks.size() && index < newsTitles.size(); index++)
            {
                sql.insertInTable(newsLinks.get(index).attr("abs:href"),newsTitles.get(index).text());
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

        Elements eventDiv;
        Elements eventTitles;
        Elements eventLinks;

        Log.i("JavService","Checking for new links");

        try
        {
            document = Jsoup.connect("http://www.tamuk.edu/").get();

            newsDiv = document.select("div#inthenews");
            newsTitles = newsDiv.select("a");
            newsLinks = newsTitles.select("[href]");

            eventDiv = document.select("div#calendar");
            eventTitles = eventDiv.select("a");
            eventLinks = eventTitles.select("[href]");

            boolean exists = true;

            ArrayList<String> allLinks = new ArrayList<>();

            //Removes any links in the database that are no longer on the webpage.
            for(int index = 0; index < eventLinks.size(); index++)
            {
                allLinks.add(eventLinks.get(index).attr("abs:href"));
            }

            for(int index = 0; index < newsLinks.size(); index++)
            {
                allLinks.add(newsLinks.get(index).attr("abs:href"));
            }

            sql.clearOld(allLinks);

            //Inserts any links that are new.
            for(int index = 0; index < newsLinks.size(); index++)
            {
                if(newsLinks.get(index).toString().contains(".html"))
                {
                    exists = sql.existInTable(newsLinks.get(index).attr("abs:href"));
                }

                if(exists)
                {
                    Log.i("JavService","Exists in table");
                }

                else
                {
                    sql.insertInTable(newsLinks.get(index).attr("abs:href").toString(),newsTitles.get(index).text());
                    newLink = true;
                    Log.i("JavService","New link inserted");
                }
            }

            //Inserts any links that are new.
            for(int index = 0; index < eventLinks.size(); index++)
            {
                if(eventLinks.get(index).toString().contains(".html"))
                {
                    exists = sql.existInTable(eventLinks.get(index).attr("abs:href"));
                }

                if(exists)
                {
                    Log.i("JavService","Exists in table");
                }

                else
                {
                    sql.insertInTable(eventLinks.get(index).attr("abs:href").toString(),eventTitles.get(index).text());
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
