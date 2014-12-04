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

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Array;

public class JavSQL extends SQLiteOpenHelper
{
    private SQLiteDatabase db;

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
        db.execSQL("Create table if not exists News (id int(10), newsurl string(500), primary key(id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.i("JavSQL","Upgrade called");
	}

    //Sees how many values are in the table
    public int getSaved()
    {
        int count;
        Cursor cursor;
        cursor = db.rawQuery("select * from News",null);
        count = cursor.getCount();

        return count;
    }

    //Insert values in table
	public void insertInTable(String text)
	{
        Cursor cursor;
        cursor = db.rawQuery("select * from News",null);

        ContentValues insert = new ContentValues();
        insert.put("id",cursor.getCount() + 1);
        insert.put("newsurl",text);

        db.insert("News",null,insert);
        cursor.close();

		Log.i("JavSQL","Url inserted in database");
	}

	public void clearOld(String url)
	{
        Cursor cursor;
        cursor = db.rawQuery("select newsurl from News",null);
        cursor.moveToFirst();

        String compared;
        boolean save = false;

        for(int index = 0; index < cursor.getCount(); index++)
        {
            compared = cursor.getString(0);

            if(url.equals(compared))
                save = true;

            cursor.moveToNext();
        }

        if(!save)
        {
            db.delete("News","newsurl = "+url,null);
            Log.i("JavSQL","Url deleted in database");
        }

        else
        {
            Log.i("JavSQL","Url not deleted from database");
        }
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

    public void closeDb()
    {
        db.close();
    }

}
