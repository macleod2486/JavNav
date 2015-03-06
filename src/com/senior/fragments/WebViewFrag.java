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


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.senior.javnav.R;

public class WebViewFrag extends Fragment 
{
	private View webFrag;
	private WebView webFragView;
	private Button reload;
	private Button forward;
	private Button back;
	
	private String url = "https://www.google.com";
	private Bundle saveState;
	private boolean restart = false;
	
	@Override
	public View onCreateView(LayoutInflater inflate, ViewGroup container, Bundle savedInstanceState)
	{
		Log.i("webFrag","webFrag started");
		
		webFrag = inflate.inflate(R.layout.web_frag, container, false);
		webFragView = (WebView)webFrag.findViewById(R.id.webFrag);
		webFragView.setWebChromeClient(new ChromeClient());
		webFragView.setWebViewClient(new Client());
		webFragView.getSettings().setBuiltInZoomControls(true);
		webFragView.getSettings().setJavaScriptEnabled(true);
		webFragView.setInitialScale(50);
		
		if(saveState == null || restart)
		{
			Log.i("WebFrag","Attempted url "+url);
			webFragView.clearHistory();
			webFragView.loadUrl(url);
			restart = false;
		}
		else
		{
			webFragView.restoreState(saveState);
		}
		
		reload = (Button)webFrag.findViewById(R.id.webRefr);
		reload.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				webFragView.reload();
			}
			
		});
		
		back = (Button)webFrag.findViewById(R.id.webBack);
		back.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(webFragView.canGoBack())
				{
					webFragView.goBack();
				}
			}
		});
		
		forward = (Button)webFrag.findViewById(R.id.webForw);
		forward.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(webFragView.canGoForward())
				{
					webFragView.goForward();
				}
			}
		});
		
		return webFrag;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		if(savedInstanceState != null)
			Log.i("WebFrag","Activity Created");
	}
	
	public void changeUrl(String url)
	{
		this.url = url;
		if(webFragView != null)
		{
			webFragView.loadUrl(this.url);
			webFragView.clearHistory();
			restart = false;
		}
	}
	
	public void loadUrl(String url, boolean restart)
	{
		this.url = url;
		this.restart = restart;
		Log.i("WebViewFrag", this.url);
	}
	
	@Override
	public void onStop()
	{
		saveState = new Bundle();
		
		if(webFragView.saveState(saveState) != null)
			Log.i("WebFrag","State Saved");
		
		super.onStop();
	}
	
	//WebChromeClient
	public class ChromeClient extends WebChromeClient 
	{
		
		public void onProgressChanged(WebView view, int progres)
		{
			ProgressBar progress;
			
			Log.i("Client","Current "+progres);
			
			progress  = (ProgressBar)webFrag.findViewById(R.id.webProgress);
			
			if(progres<100 && progress!=null)
			{
				progress.setProgress(progres);
			}
			
			if(progres==100 && progress!=null)
			{
				progress.setVisibility(View.GONE);
			}
		}

	}
	
	//WebViewClient
	public class Client extends WebViewClient
	{
	
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			Log.i("Client","Page started "+url);
			ProgressBar progress = (ProgressBar)webFrag.findViewById(R.id.webProgress);
			progress.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
            String[] businessDocs = {"pdf", "docx", "doc", "pptx", "ppt", "xls", "xlsx", "zip", "gz", "7z", "777"};

            boolean  isWebPage = true;

            if(url.startsWith("mailto:"))
            {
                isWebPage = false;

                Intent mailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                startActivity(mailIntent);
            }

            //Checks each extension to see if it is a eligible file.
            for(int index = 0; index < businessDocs.length; index++)
            {
                if(url.endsWith(businessDocs[index]))
                {
                    isWebPage = false;

                    Uri source = Uri.parse(url);
                    String fileName = url.split("/")[url.split("/").length-1];

                    DownloadManager.Request request = new DownloadManager.Request(source);

                    request.setDescription("Downloading");
                    request.setTitle(fileName);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    {
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    }

                    // Save the file in the "Downloads" folder of SDCARD
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

                    // get download service and enqueue file
                    DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                }
            }

            if(isWebPage)
            {
                view.loadUrl(url);
            }

			return true;
		}
		
		@Override
		public void onReceivedError(WebView view, int errorVode, String description, String failingUrl)
		{
			ProgressBar progress = (ProgressBar)webFrag.findViewById(R.id.webProgress);
			progress.setVisibility(View.INVISIBLE);
		}
		
	}
	
}
