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

import com.actionbarsherlock.app.SherlockFragment;
import com.senior.javnav.R;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class CustomWebpage extends SherlockFragment 
{
	View customView;
	WebView customClient;
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		customView = inflate.inflate(R.layout.custom_fragment, container,false);
		customClient = (WebView)customView.findViewById(R.id.custom);
		
		SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(getActivity());
		String url = shared.getString("webURL", "https://www.google.com");
		if(url.charAt(0)!='h')
			url="http://"+url;
		
		customClient.getSettings().setBuiltInZoomControls(true);
		customClient.setWebViewClient(new Client());
		customClient.setWebChromeClient(new ChromeClient());
		customClient.getSettings().setJavaScriptEnabled(true);
		customClient.setInitialScale(25);
		customClient.loadUrl(url);
		
		return customView;
	}
	

public class ChromeClient extends WebChromeClient 
{
	
	public void onProgressChanged(WebView view, int progres)
	{
		ProgressBar progress;
		Log.i("Client","Current "+progres);
		progress  = (ProgressBar)customView.findViewById(R.id.webProgress);
		if(progres<100&&progress!=null)
		{
				//progress.setVisibility(1);
				progress.setProgress(progres);
		}
		if(progres==100&&progress!=null)
			progress.setVisibility(View.GONE);
	}

}
//WebViewClient

public class Client extends WebViewClient
{

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon)
	{
		Log.i("Client","Page started");
		ProgressBar progress = (ProgressBar)customView.findViewById(R.id.webProgress);
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
