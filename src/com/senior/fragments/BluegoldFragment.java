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

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.senior.javnav.R;

import com.actionbarsherlock.app.SherlockFragment;

public class BluegoldFragment extends SherlockFragment 
{
	View bluegold;
	WebView bluegoldview;
	String url = "https://www.tamuk.edu/bluegold";
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("BLUE","Bluegold page loading");
		bluegold = inflate.inflate(R.layout.bluegold, container, false);
		bluegoldview = (WebView)bluegold.findViewById(R.id.webBluegold);
		bluegoldview.setWebChromeClient(new ChromeClient());
		bluegoldview.setWebViewClient(new Client());
		bluegoldview.getSettings().setJavaScriptEnabled(true);
		bluegoldview.getSettings().setBuiltInZoomControls(true);
		bluegoldview.setInitialScale(50);
		
		if(savedInstanceState!=null)
		{
			bluegoldview.restoreState(savedInstanceState);
		}
		else
		{
			bluegoldview.loadUrl(url);
		}
		Log.i("Blue","Bluegold page finished loading");
		return bluegold;
	}
	
	public void onSaveInstanceState(Bundle outstate)
	{
		bluegoldview.saveState(outstate);
	}
	//WebChromeClient
	public class ChromeClient extends WebChromeClient 
	{
		
		public void onProgressChanged(WebView view, int progres)
		{
			ProgressBar progress;
			Log.i("Client","Current "+progres);
			progress  = (ProgressBar)bluegold.findViewById(R.id.webProgress);
			if(progres<100&&progress!=null)
			{
					progress.setProgress(progres);
			}
			if(progres==100&&progress!=null)
				progress.setVisibility(View.GONE);
		}

	}
	
	public void loadUrl(String url)
	{
		this.url = url;
	}
	
	public String currentUrl()
	{
		String currentUrl = bluegoldview.getUrl();
		return currentUrl;
	}
	
	//WebViewClient

	public class Client extends WebViewClient
	{
	
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			Log.i("Client","Page started");
			ProgressBar progress = (ProgressBar)bluegold.findViewById(R.id.webProgress);
			progress.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}
		
		
	}
}
