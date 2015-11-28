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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.senior.javnav.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ArticleContent extends Fragment
{
	
	private View articleView;
	private Button reloadButton;
	private ProgressBar progress;
	
	private String url;
	private String articleTitle;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		refreshArticle();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		 //Obtains each 
		articleView=inflater.inflate(R.layout.articles_fragment,container,false);
		TextView title = (TextView)articleView.findViewById(R.id.title);
		progress = (ProgressBar)articleView.findViewById(R.id.progress);
		SpannableString NewTitle = new SpannableString(articleTitle);
		 
		 //Sets the options for display
		NewTitle.setSpan(new UnderlineSpan(), 0, NewTitle.length(), 0);
		title.setText(NewTitle);
		 
		reloadButton = (Button)articleView.findViewById(R.id.refresh);
		reloadButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				refreshArticle();
			}
		}); 
		return articleView;
	}
	
	public void loadArticleInfo(String url, String title)
	{
		this.url = url;
        this.url = this.url.replaceAll(" ","%20");
		this.articleTitle = title;
	}
	
	private void refreshArticle()
	{
		reloadButton.setVisibility(View.INVISIBLE);
		progress.setVisibility(View.VISIBLE);
		new getArticles().execute();
	}

	//Class that obtains the articles.
	private class getArticles extends AsyncTask<String, Void, ArrayList<String>>
	{
		private boolean completed = false;
		private String article;
		protected ArrayList<String> doInBackground(String...params)
		{
			Log.i("Article","do in background");
			try
			{
                String content = "";
				Document document = Jsoup.connect(url).get();
				Elements newsContent = document.select("div#content");
                Elements paragraphs = newsContent.select("p");

                for(Element paragraph : paragraphs)
                {
                    content += paragraph.text()+"\n";
                }

				article = content;
				Log.i("Article",newsContent.text());
				
				completed = true;
			}
			catch(Exception e)
			{
				completed = false;
				Log.i("Article","Error "+e);
			}
			return null;
		}
		//When the article is post executed
		@Override
		protected void onPostExecute(ArrayList<String> strings)
		{
			Log.i("Article","on post execute");
			TextView text = (TextView)articleView.findViewById(R.id.text);
			text.setText(article);
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
	}
}
