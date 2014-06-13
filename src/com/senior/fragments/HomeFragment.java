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
import org.jsoup.select.Elements;

import com.senior.javnav.R;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeFragment extends ListFragment
{
	private ListView mListView;
	private View view;
	private TextView titletext;
	private Button reloadButton;
	private ProgressBar progress;
	
	public static int linkIndex;
	public static ArrayList<String> eventtitles;
	public static ArrayList<String> eventlinks = new ArrayList<String>();
	public static String TitleChosen;
	
	ArticleContent articles;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		Log.i("HomeFrag","Activity Created");
		super.onActivityCreated(savedInstanceState);
		getArticles();
	}
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("HomeFrag","View Created"); 
		
		view=inflater.inflate(R.layout.home_fragment,container,false);
		
		titletext = (TextView)view.findViewById(R.id.calenderevents);
		
		SpannableString NewTitle = new SpannableString("Calendar Of Events");
		
		NewTitle.setSpan(new UnderlineSpan(), 0, NewTitle.length(), 0);
		
		titletext.setText(NewTitle);
		
		mListView=(ListView)view.findViewById(android.R.id.list);
		
		reloadButton = (Button)view.findViewById(R.id.refresh);
		reloadButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				getArticles();
			}
			
		});
		
		progress = (ProgressBar)view.findViewById(R.id.progress);
		
		return view;
	}
	
	private void getArticles()
	{
		reloadButton.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.VISIBLE);
		new getDivs().execute();
	}
	
	private class getDivs extends AsyncTask<String, Void, ArrayList<String>>
	{
		boolean completed = false;
		protected ArrayList<String> doInBackground(String...params)
		{
			eventtitles= new ArrayList<String>();
			try
			{
				Document document = Jsoup.connect("http://www.tamuk.edu/").get();
				Elements calendar=document.select("div#calendar");
				Elements titles=calendar.select("a");
				Elements links = titles.select("[href]");
				
				for(int index = 0; index < titles.size()&&index < links.size(); index++)
				{
					if(links.get(index).toString().contains(".html"))
					{
						if(isCancelled())
							break;
						eventtitles.add(titles.get(index).text());
						eventlinks.add(links.get(index).attr("abs:href").toString());
					}
					
				}
				completed = true;
			}
			catch(Exception e)
			{
				completed = false;
				e.printStackTrace();
			}
			return eventtitles;
		}
		@Override
		protected void onPostExecute(ArrayList<String> strings)
		{
			try
			{
				Log.i("home frag","Results: "+eventtitles.size());
				mListView.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,eventtitles)
				{
					//Styling the items within the listview
					@Override
					public View getView(int position, View convertView, ViewGroup parent)
					{
						View view = super.getView(position,convertView,parent);
						TextView listItem = (TextView)view.findViewById(android.R.id.text1);
						listItem.setBackgroundColor(Color.BLACK);
						listItem.setTextColor(Color.WHITE);
						return view;
					}
				});
				
				//If the information was not complete downloaded then it will bring back the
				//reload button
				if(completed)
				{
					reloadButton.setVisibility(View.INVISIBLE);
					progress.setVisibility(View.INVISIBLE);
				}
				else
				{
					reloadButton.setVisibility(View.VISIBLE);
					progress.setVisibility(View.INVISIBLE);
				}
					
			}
			catch(Exception e)
			{
				Log.i("Home","Error on post execute");
			}
		}
	}
	
	public void onListItemClick(ListView mListView, View view, int position, long id)
	{
		ArticleContent articles = new ArticleContent();
	    articles.loadArticleInfo(eventlinks.get(position), eventtitles.get(position));
	    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.container, articles).commit();
	}

}
	
