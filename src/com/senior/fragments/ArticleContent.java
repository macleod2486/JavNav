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
package com.senior.fragments;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockFragment;
import com.senior.javnav.R;


//import android.support.v4.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ArticleContent extends SherlockFragment{
	
	View view;
	int i=0;
	int j=HomeFragment.m;;
	String eventstring;
	String eventlink;
	ArrayList<String> events = HomeFragment.eventtitles;
	ArrayList<String> links = HomeFragment.eventlinks;
	ArrayList<String> eventcontent;
	String PassedTitle = HomeFragment.TitleChosen;
	
	//@Override
	 public void onActivityCreated(Bundle savedInstanceState)
	 {
		 super.onActivityCreated(savedInstanceState);
		 new getArticles().execute();
	 }
	//@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		 
		 view=inflater.inflate(R.layout.articles_fragment,container,false);
		 ImageView banner = (ImageView)view.findViewById(R.id.banner);
		 ImageView footerImg = (ImageView)view.findViewById(R.id.footerimg);
		 TextView title = (TextView)view.findViewById(R.id.title);
		 SpannableString NewTitle = new SpannableString(PassedTitle);
		 NewTitle.setSpan(new UnderlineSpan(), 0, NewTitle.length(), 0);
		 title.setText(NewTitle);
		 title.setBackgroundColor(Color.BLACK);
		 banner.setImageResource(R.drawable.gohogs);
		 footerImg.setImageResource(R.drawable.tamukbanner);
		 return view;
		}

private class getArticles extends AsyncTask<String, Void, ArrayList<String>>
{
		
		protected ArrayList<String> doInBackground(String...params)
		{
			Log.i("Article","do in background");
			eventcontent= new ArrayList<String>();
			String connection = links.get(j);
			try
			{
					Document document = Jsoup.connect(connection).get();
					Elements divisions1=document.select("div#newsbody");
					Elements divisions = divisions1.select("div#newscontent");
					
					for(Element Division :divisions)
					{
						if(isCancelled())
							break;
						eventcontent.add(Division.text());
						
					}
			}
			catch(Exception e)
			{
				Log.i("Article","Error "+e);
			}
			Log.e("Article","Results: "+eventcontent.size());
			return eventcontent;
		}
		//When the article is post executed
		@Override
		protected void onPostExecute(ArrayList<String> strings)
		{
			Log.i("Article","on post execute");
			for(int l=0; l<eventcontent.size(); l++)
			{
			{	
					if(isCancelled())
						break;
					eventstring = "\n"+eventcontent.get(l)+"\n";
					
			}
				TextView tv = (TextView)view.findViewById(R.id.text);
				tv.setBackgroundColor(Color.BLACK);
				//tv.setTextColor(Color.YELLOW);
				tv.setText(eventstring);
			}	
		}
		
		}
}
