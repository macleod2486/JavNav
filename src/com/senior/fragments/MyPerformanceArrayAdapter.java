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
import com.senior.javnav.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPerformanceArrayAdapter extends BaseAdapter{
	Context mContext;
	private LayoutInflater mInflater;
	private final String[] title;
	public static String TitleChosen = "Calendar of Events";
	int layoutResourceId;
	
	static class ViewHolder{
	static TextView title;
	ImageView icon;
	}
	public MyPerformanceArrayAdapter(Context context2, String[] title){
		//super(context2, R.layout.listview_item_row, title);
		mContext = context2;
		mInflater = LayoutInflater.from(context2);
		//this.context=context2;
		this.title=title;
	}
	public int getCount() {
		return title.length;
	} 
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public long getItemId(int position) {
		return position;
	} 
	
	@SuppressWarnings("static-access")
	public View getView(int position, View convertView, ViewGroup parent){
	try {
	//ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_row, parent, false);
		}
		ViewHolder holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.txtTitle);  
			//if (position == 0) {
				//convertView.setTag(holder);
		
			holder.title.setText(title[position]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return convertView;
	}
	

}
