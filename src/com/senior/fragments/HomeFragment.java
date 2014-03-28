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
package com.senior.fragments;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockListFragment;
import com.senior.javnav.R;


import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ListView;
import android.widget.TextView;

public class HomeFragment extends SherlockListFragment
{
	ListView mListView;
	ImageView image;
	View view;
	String[] titles;
	static int m;
	TextView titletext;
	static ArrayList<String> eventtitles;
	public static ArrayList<String> eventlinks = new ArrayList<String>();
	static String TitleChosen;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		 super.onActivityCreated(savedInstanceState);
		 mListView.setBackgroundColor(Color.BLACK);
		 new getDivs().execute();
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		 
		view=inflater.inflate(R.layout.home_fragment,container,false);
		image=(ImageView)view.findViewById(R.id.imgIcon);
		image.setImageResource(R.drawable.students);
		titletext = (TextView)view.findViewById(R.id.calenderevents);
		SpannableString NewTitle = new SpannableString("Calendar Of Events");
		NewTitle.setSpan(new UnderlineSpan(), 0, NewTitle.length(), 0);
		titletext.setText(NewTitle);
		titletext.setBackgroundColor(Color.BLACK);
		mListView=(ListView)view.findViewById(android.R.id.list);
		return view;
	}
	
	
	private class getDivs extends AsyncTask<String, Void, ArrayList<String>>
	{
			protected ArrayList<String> doInBackground(String...params)
			{
				eventtitles= new ArrayList<String>();
				try
				{
					Document document = Jsoup.connect("http://www.tamuk.edu/").get();
					Elements divisions=document.select("div#calendar");
					Elements divisions2=divisions.select("a");
					Elements links = divisions2.select("[href]");
					
					for(int index = 0; index < divisions2.size()&&index < links.size(); index++)
					{
							if(isCancelled())
								break;
							eventtitles.add(divisions2.get(index).text());
							eventlinks.add(links.get(index).attr("abs:href").toString());
						
					}
						
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return eventtitles;
			}
			@Override
			protected void onPostExecute(ArrayList<String> strings)
			{
				try
				{
					int eventtitlesize=eventtitles.size();
					titles = new String[eventtitlesize];
					for(int index=0; index<eventtitlesize; index++)
					{
						if(isCancelled())
							break;
						String titleDetail = eventtitles.get(index);
						titles[index]= titleDetail.toString();
						
					}
					Log.i("home frag","Results: "+eventtitles.size());
					mListView.setAdapter(new MyPerformanceArrayAdapter(getActivity(),titles));
				}
				catch(Exception e)
				{
					Log.i("Home","Error on post execute");
				}
			}
			
				
	}

	public void onListItemClick(ListView mListView, View view, int position, long id)
	{
		
	    String chosen = titles[position];
	    for(int index = 0; index<titles.length; index++)
	    {
			if(chosen.equals(titles[index]))
			{
				m=index;
				TitleChosen = chosen;
				Fragment articles = new ArticleContent();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.container, articles);
				ft.addToBackStack(null);
				ft.commit();
			}
		}
		
	}

}
	
