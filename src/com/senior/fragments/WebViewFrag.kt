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
package com.senior.fragments

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.senior.javnav.R

class WebViewFrag : Fragment() {

    lateinit private var webFrag: View;
    private var webFragView: WebView? = null
    private var reload: Button? = null
    private var forward: Button? = null
    private var back: Button? = null
    private var url = "https://www.google.com"
    private var saveState: Bundle? = null
    private var restart = false

    override fun onCreateView(inflate: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i("webFrag", "webFrag started")
        webFrag = inflate.inflate(R.layout.web_frag, container, false)
        webFragView = webFrag.findViewById<View>(R.id.webFrag) as WebView
        webFragView!!.webChromeClient = ChromeClient()
        webFragView!!.webViewClient = Client()
        webFragView!!.settings.builtInZoomControls = true
        webFragView!!.settings.javaScriptEnabled = true
        if (saveState == null || restart) {
            Log.i("WebFrag", "Attempted url $url")
            webFragView!!.clearHistory()
            webFragView!!.loadUrl(url)
            restart = false
        } else {
            webFragView!!.restoreState(saveState)
        }
        reload = webFrag.findViewById<View>(R.id.webRefr) as Button
        reload!!.setOnClickListener { webFragView!!.reload() }
        back = webFrag.findViewById<View>(R.id.webBack) as Button
        back!!.setOnClickListener {
            if (webFragView!!.canGoBack()) {
                webFragView!!.goBack()
            }
        }
        forward = webFrag.findViewById<View>(R.id.webForw) as Button
        forward!!.setOnClickListener {
            if (webFragView!!.canGoForward()) {
                webFragView!!.goForward()
            }
        }
        return webFrag
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) Log.i("WebFrag", "Activity Created")
    }

    fun changeUrl(url: String) {
        this.url = url
        if (webFragView != null) {
            webFragView!!.loadUrl(this.url)
            webFragView!!.clearHistory()
            restart = false
        }
    }

    fun loadUrl(url: String, restart: Boolean) {
        this.url = url
        this.restart = restart
        Log.i("WebViewFrag", this.url)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 23) {
            val writeExternal = requireActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val granted = PackageManager.PERMISSION_GRANTED
            if (writeExternal != granted) {
                val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requireActivity().requestPermissions(permissions, 0)
            } else {
                Log.i("MainActivity", "Write permissions granted")
            }
        }
    }

    override fun onStop() {
        saveState = Bundle()
        if (webFragView!!.saveState(saveState) != null) Log.i("WebFrag", "State Saved")
        super.onStop()
    }

    //WebChromeClient
    inner class ChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, progres: Int) {
            val progress: ProgressBar
            Log.i("Client", "Current $progres")
            progress = webFrag!!.findViewById<View>(R.id.webProgress) as ProgressBar
            if (progres < 100 && progress != null) {
                progress.progress = progres
            }
            if (progres == 100 && progress != null) {
                progress.visibility = View.GONE
            }
        }
    }

    //WebViewClient
    inner class Client : WebViewClient() {
        override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
            Log.i("Client", "Page started $url")
            val progress = webFrag.findViewById<View>(R.id.webProgress) as ProgressBar
            progress.visibility = View.VISIBLE
            super.onPageStarted(view, url, favicon)
        }

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            val businessDocs = arrayOf("pdf", "docx", "doc", "pptx", "ppt", "xls", "xlsx", "zip", "gz", "7z", "777")
            var isWebPage = true
            if (url.startsWith("mailto:")) {
                isWebPage = false
                val mailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                startActivity(mailIntent)
            }

            //Checks each extension to see if it is a eligible file.
            for (index in businessDocs.indices) {
                if (url.endsWith(businessDocs[index])) {
                    isWebPage = false
                    var permissionGranted = false
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (activity!!.baseContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            permissionGranted = true
                        } else {
                            Toast.makeText(activity!!.baseContext, "Please enable write permissions to save file", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        permissionGranted = true
                    }
                    if (permissionGranted) {
                        val source = Uri.parse(url)
                        val fileName = url.split("/").toTypedArray()[url.split("/").toTypedArray().size - 1]
                        val request = DownloadManager.Request(source)
                        request.setDescription("Downloading")
                        request.setTitle(fileName)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            request.allowScanningByMediaScanner()
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        }

                        // Save the file in the "Downloads" folder of SDCARD
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

                        // get download service and enqueue file
                        val manager = activity!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        manager.enqueue(request)
                    }
                }
            }
            if (isWebPage) {
                view.loadUrl(url)
            }
            return true
        }

        override fun onReceivedError(view: WebView, errorVode: Int, description: String, failingUrl: String) {
            val progress = webFrag!!.findViewById<View>(R.id.webProgress) as ProgressBar
            progress.visibility = View.INVISIBLE
        }
    }
}