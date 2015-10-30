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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class JavSQL extends SQLiteOpenHelper
{
    private SQLiteDatabase db;

    private ArrayList<String> listOfNewEvents = new ArrayList<String>();

	public JavSQL(Context context, String name, CursorFactory factory,
			int version)
	{
        super(context, name, factory, version);

        db = this.getWritableDatabase();

        Log.i("JavSQL","Initializer called.");
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
        Log.i("JavSQL","On create called");
        db.execSQL("Create table if not exists News (id int not null, newstitle string(300), newsurl string(500), seen int(2), primary key(id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.i("JavSQL","Upgrade called");
        db.execSQL("Create table if not exists News (id int not null, newstitle string(300), newsurl string(500), seen int(2), primary key(id))");
	}

    //Sees how many values are in the table
    public int getSaved()
    {
        int count;
        Cursor cursor;
        cursor = db.rawQuery("select * from News",null);
        count = cursor.getCount();
        cursor.close();

        return count;
    }

    //Insert values in table
	public void insertInTable(String newsurl, String newstitle)
	{
        Cursor cursor;
        cursor = db.rawQuery("select max(id) from News",null);
        cursor.moveToFirst();

        ContentValues insert = new ContentValues();
        insert.put("id",cursor.getInt(0)+1);
        insert.put("newsurl",newsurl);
        insert.put("newstitle",newstitle);
        insert.put("seen",0);

        db.insert("News",null,insert);
        cursor.close();

        listOfNewEvents.add(newstitle);

		Log.i("JavSQL","Url inserted in database");
	}

	public void clearOld(ArrayList<String> urls)
	{
        //TODO: Clean up this and make it more readable.

        String deleteOld = "(select newsurl from News where newsurl = '"+urls.get(0)+"'";
        String finalQuery;

        for(int index = 1; index < urls.size(); index++)
        {
            deleteOld += " or newsurl = '"+urls.get(index)+"' ";
        }

        deleteOld += ") as x";

        finalQuery = " ( select newsurl from "+deleteOld+" )";

        finalQuery = "delete from News where newsurl not in "+finalQuery;

        db.execSQL(finalQuery);

        Log.i("JavSQL","Deleted old entries.");

	}

    public boolean existInTable(String urltext)
    {
        boolean exists = false;

        String query = "select * from News where newsurl = ?;";

        Cursor cursor;
        cursor = db.rawQuery(query, new String[]{urltext});

        if (cursor.getCount() != 0)
        {
            exists = true;
        }

        cursor.close();

        return exists;
    }


    //Returns all links in database
    public ArrayList<String> returnLinks()
    {
        ArrayList<String> links = new ArrayList<String>();

        Cursor cursor;
        cursor = db.rawQuery("select newsurl from News",null);
        cursor.moveToFirst();

        for(int index = 0; index < cursor.getCount(); index++)
        {
            links.add(index,cursor.getString(0));

            cursor.moveToNext();
        }

        cursor.close();

        return links;
    }

    //Returns all titles in database
    public ArrayList<String> returnTitles()
    {
        ArrayList<String> titles = new ArrayList<String>();

        Cursor cursor;
        cursor = db.rawQuery("select newstitle from News",null);
        cursor.moveToFirst();

        for(int index = 0; index < cursor.getCount(); index++)
        {
            titles.add(index,cursor.getString(0));

            cursor.moveToNext();
        }

        cursor.close();

        return titles;
    }

    public ArrayList<String> newEventsList()
    {
        return listOfNewEvents;
    }

    //Checks to see if a link has been seen or not
    public boolean seen(String title)
    {
        boolean hasBeenScene = false;

        String query = "select seen from News where newstitle = ?;";

        Cursor cursor;
        cursor = db.rawQuery(query, new String[]{title});
        cursor.moveToFirst();

        if (cursor.getInt(0) == 1)
        {
            hasBeenScene = true;
            Log.i("JavSQL","Link has been seen");
        }

        cursor.close();

        return hasBeenScene;
    }

    //Sets a selected link to be seen
    public void setSeen(String title)
    {
        String clause = "newstitle = ?;";

        ContentValues insert = new ContentValues();
        insert.put("seen", "1");

        db.update("News",insert,clause,new String[]{title});

        Log.i("JavSQL","Link is now seen");
    }

    public void closeDb()
    {
        Log.i("JavSQL","Closing database");
        db.close();
    }

}
