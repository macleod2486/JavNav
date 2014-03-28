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


import com.actionbarsherlock.app.SherlockFragment;
import com.senior.javnav.R;
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

public class BlackboardFrag extends SherlockFragment 
{
	View blackboard;
	WebView blackboardView;
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("Blackboard","Blackboard started");
		blackboard = inflate.inflate(R.layout.blackboard, container, false);
		blackboardView = (WebView)blackboard.findViewById(R.id.blackboardView);
		blackboardView.setWebChromeClient(new ChromeClient());
		blackboardView.setWebViewClient(new Client());
		blackboardView.getSettings().setBuiltInZoomControls(true);
		blackboardView.getSettings().setJavaScriptEnabled(true);
		blackboardView.setInitialScale(50);
		
		if(savedInstanceState!=null)
		{
			blackboardView.restoreState(savedInstanceState);
		}
		else
		{
			blackboardView.loadUrl("https://blackboard.tamuk.edu");
		}
		Log.i("Blackboard","Blackboard fragment finished");
		return blackboard;
	}
	
	
	public void onSaveInstanceState(Bundle outstate)
	{
		blackboardView.saveState(outstate);
	}
	//WebChromeClient
	public class ChromeClient extends WebChromeClient 
	{
		
		public void onProgressChanged(WebView view, int progres)
		{
			ProgressBar progress;
			Log.i("Client","Current "+progres);
			progress  = (ProgressBar)blackboard.findViewById(R.id.webProgress);
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
			ProgressBar progress = (ProgressBar)blackboard.findViewById(R.id.webProgress);
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
