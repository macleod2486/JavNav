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
//Java imports
import java.util.ArrayList;

//JSoup imports
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//Project imports
import com.senior.javnav.R;

//Android imports
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ArticleContent extends Fragment
{
	
	View articleView;
	Button reloadButton;
	
	int index=HomeFragment.linkIndex;
	String eventstring;
	String eventlink;
	ArrayList<String> events = HomeFragment.eventtitles;
	ArrayList<String> links = HomeFragment.eventlinks;
	ArrayList<String> eventcontent;
	String PassedTitle = HomeFragment.TitleChosen;
	
	@Override
	 public void onActivityCreated(Bundle savedInstanceState)
	 {
		 super.onActivityCreated(savedInstanceState);
		 new getArticles().execute();
	 }
	@Override
	 public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	 {
		 //Obtains each 
		 articleView=inflater.inflate(R.layout.articles_fragment,container,false);
		 TextView title = (TextView)articleView.findViewById(R.id.title);
		 SpannableString NewTitle = new SpannableString(PassedTitle);
		 
		 //Sets the options for display
		 NewTitle.setSpan(new UnderlineSpan(), 0, NewTitle.length(), 0);
		 title.setText(NewTitle);
		 title.setBackgroundColor(Color.BLACK);
		 
		 reloadButton = (Button)articleView.findViewById(R.id.refresh);
		 
		 return articleView;
	}
	
	public void reloadArticle()
	{
		reloadButton.setVisibility(View.INVISIBLE);
		new getArticles().execute();
	}

	//Class that obtains the articles.
	private class getArticles extends AsyncTask<String, Void, ArrayList<String>>
	{
			private boolean completed = false;
			
			protected ArrayList<String> doInBackground(String...params)
			{
				Log.i("Article","do in background");
				eventcontent= new ArrayList<String>();
				String connection = links.get(index);
				try
				{
						Document document = Jsoup.connect(connection).get();
						Elements news=document.select("div#newsbody");
						Elements newsContent = news.select("div#newscontent");
						
						for(Element Division :newsContent)
						{
							if(isCancelled())
								break;
							eventcontent.add(Division.text());
							
						}
						completed = true;
				}
				catch(Exception e)
				{
					completed = false;
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
				TextView tv = (TextView)articleView.findViewById(R.id.text);
				for(int limit=0; limit<eventcontent.size(); limit++)
				{
					if(isCancelled())
						break;
					eventstring = "\n"+eventcontent.get(limit)+"\n";
					tv.setBackgroundColor(Color.BLACK);
					tv.setText(eventstring);
				}	
				if(completed)
				{
					reloadButton.setVisibility(View.INVISIBLE);
				}
				else
				{
					reloadButton.setVisibility(View.VISIBLE);
				}
			}
			
		}
}
