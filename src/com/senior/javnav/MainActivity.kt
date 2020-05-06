/*
*
*   JavNav 
*    a simple application for general use of the Texas A&M-Kingsville Campus. 
*    
*    Copyright (C) 2019  Manuel Gonzales Jr.
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
package com.senior.javnav

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.legacy.app.ActionBarDrawerToggle
import com.senior.fragments.GoogleFragment
import com.senior.fragments.WebViewFrag

class MainActivity : AppCompatActivity() {
    //News fragment
    var news = WebViewFrag()

    //Google map fragment
    var Google = GoogleFragment()

    //Bluegold fragment
    var bluegold = WebViewFrag()

    //Preferences fragment
    var preferences = Preferences()

    //Blackboard fragment
    var blackboard = WebViewFrag()
    lateinit var drawer: DrawerLayout
    lateinit var drawerToggle: ActionBarDrawerToggle
    var index = 0
    var newsUrl = "https://www.tamuk.edu/news/"
    var bluegoldurl = "https://as2.tamuk.edu:9203/PROD/twbkwbis.P_WWWLogin"
    var blackboardurl = "https://blackboard.tamuk.edu"
    var title = "News"

    //Manually handle the back button being pressed.
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.i("Main", "Drawer closed")
            drawer.closeDrawers()
        } else if (index != 0 && !news.isAdded) {
            supportFragmentManager.beginTransaction().replace(R.id.container, news, "news").commit()
            title = "News"
            supportActionBar!!.setTitle(title)
        } else {
            super.onBackPressed()
        }
    }

    /*
	 * Methods that are called to handle the activity lifecycle
	*/
    //When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Sets the layout to the activity main layout
        setContentView(R.layout.activity_main)

        //Checks to see if the device has internet access and alerts user if not
        if (!online()) {
            val connectBuild = AlertDialog.Builder(this)
            connectBuild.setTitle("Warning!")
            connectBuild.setMessage("You need to be connected to the internet!").setCancelable(true)
            val connect = connectBuild.create()
            connect.show()
        }

        //Make the actionbar clickable to bring out the drawer
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        //Seeds initial webpages.
        news.loadUrl(newsUrl, true)
        bluegold.loadUrl(bluegoldurl, true)
        blackboard.loadUrl(blackboardurl, true)

        //Configures the drawer
        drawer = findViewById(R.id.drawer)
        drawerToggle = object : ActionBarDrawerToggle(this, drawer, R.drawable.ic_launcher, R.string.drawer_open, R.string.drawer_close) {
            override fun onDrawerClosed(view: View) {
                supportActionBar!!.setTitle(title)
                super.onDrawerClosed(view)
            }

            override fun onDrawerOpened(drawerView: View) {
                supportActionBar!!.setTitle(R.string.drawer_open)
                super.onDrawerOpened(drawerView)
            }
        }
        drawer.setDrawerListener(drawerToggle)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        //Sets up the listview within the drawer
        val menuList = resources.getStringArray(R.array.list)
        val list = findViewById<View>(R.id.optionList) as ListView
        list.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menuList)
        list.onItemClickListener = OnItemClickListener { _, _, position, _ ->
            Log.i("MainActivity", "Position $position")
            if (position == 0) {
                index = 0
                supportFragmentManager.beginTransaction().replace(R.id.container, news, "news").commit()
                title = "News"
            } else if (position == 1) {
                index = 1
                supportFragmentManager.beginTransaction().replace(R.id.container, Google, "map").commit()
                title = "Map"
            } else if (position == 2) {
                index = 2
                supportFragmentManager.beginTransaction().replace(R.id.container, bluegold, "bluegold").commit()
                title = "Blue and Gold"
            } else if (position == 3) {
                index = 3
                supportFragmentManager.beginTransaction().replace(R.id.container, blackboard, "blackboard").commit()
                title = "Blackboard"
            } else if (position == 4) {
                //startActivity(Intent(this@MainActivity, Preferences::class.java))
                index = 4
                supportFragmentManager.beginTransaction().replace(R.id.container, preferences, "preferences").commit()
                title = "Preferences"
            }
            drawer.closeDrawers()
        }

        //Displays the first fragment
        supportFragmentManager.beginTransaction().replace(R.id.container, news, "news").commit()
        supportActionBar!!.setTitle(title)
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val action = supportActionBar
            action!!.hide()
        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val action = supportActionBar
            action!!.show()
        }
    }

    //When the activity is destroyed
    override fun onDestroy() {
        Log.i("Main", "Destroy called")
        super.onDestroy()
    }

    //Toggles open the drawer
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers()
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
        return true
    }

    //Checks if the device is online
    private fun online(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null && info.isConnectedOrConnecting
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Google.TAMUK.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(this, "Please allow location to be enabled to view yourself on the map", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}