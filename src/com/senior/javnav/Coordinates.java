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

package com.senior.javnav;

import android.util.Log;



public class Coordinates 
{

	public double longit[]={-97.878292,-97.880716,-97.879542,-97.879547,-97.881585,-97.879493,-97.881526,-97.882808,-97.882862,
					 -97.879279,-97.882454,-97.881896,-97.879493,-97.880191,-97.880148,-97.878512,-97.882969,-97.878914,
					 -97.880491,-97.880287,-97.883635,-97.883238,-97.884954,-97.883688,-97.881875,-97.884675,-97.880931,
					 -97.879268,-97.883281,-97.878420,-97.879021,-97.883388,-97.882926,-97.883592,-97.879504,-97.879289,
					 -97.879922,-97.881746,-97.885158,-97.885115,-97.880234,-97.884321,-97.881778,-97.878233,-97.881816,
					 -97.882851,-97.880325,-97.878844,-97.879107,-97.880073,-97.883989,-97.884927,-97.884783};
	
	//Latitude of every building in the names array
	public double latit[]={27.525846,27.525998,27.529832,27.523838,27.529261,27.524561,27.524638,27.526902,27.521812,
					27.529471,27.525066,27.526807,27.522230,27.522230,27.526074,27.524152,27.523410,27.526017,
					27.524733,27.523781,27.524752,27.529556,27.524657,27.526103,27.526008,27.525304,27.523068,
					27.528957,27.522858,27.524209,27.525356,27.525617,27.524162,27.527568,27.523305,27.528938,
					27.525513,27.523505,27.527730,27.526740,27.523286,27.527948,27.522230,27.524794,27.527815,
					27.526141,27.524457,27.522264,27.524171,27.526983,27.525851,27.523030,27.525832};
	
	//Names of all the 
	public String names[]={"AL Gross Hall","Al Kleberg Hall","Athletic Offices","Bailey Arts","Baseball Field","Bellamah Music","Biology-Earth Science","Business Administration","Center for Young Children",
			"Clement","College Hall","College of Pharmacy","Conner Museum","Cousins Hall","Dotterweich Engineering","Drama/Arts Building","Eckhardt Hall","Engineering Complex",
			"Health And Recreation","Hill Hall","Human Sciences","Intramural Fields","J.C. Martin Hall","JW Howe AG","James Jernigan","Javelina Dining","Javelina House",
			"Javelina Stadium","John Lynch Hall","Jones Auditorium","Karr Veterans","Kleberg AG","Lewis Hall","Life Services","Manning Hall","McCulley Hall",
			"McNeil Engineering","Memorial SUB","Mesquite Grove","Mesquite Village","Nierman Science","Physical Plant","Poteet Hall","Presidents Home","Recreation Center",
			"Rhode Hall","Sam Fore Hall","Seale Hall","Speech Building","Steike Physical ED","Support Services","Turner-Bishop Hall","University Village"};
			
	
	public double latitude(String name)
	{
		double result=0;
		int selected=0;
		while(selected<names.length)
		{
			if(names[selected].contains(name))
			{
				Log.i("Coord","Found! "+selected);
				result=latit[selected];
				break;
			}
			selected++;
		}
		return result;
	}
	public double longitude(String name)
	{
		double result=0;
		int selected=0;
		while(selected<names.length)
		{
			if(names[selected].contains(name))
			{
				result=longit[selected];
				break;
			}
			selected++;
		}
		return result;
	}
/*
		//AL Gross Hall - Lat=27.525846 Long=-97.878292
		//Al Kleberg Hall - Lat=27.525998 Long=-97.880716
		//Athletic Offices - Lat=27.529832 Long=-97.879300
		//Bailey Arts - Lat=27.523838 Long=	-97.879547
		//Baseball Field - Lat=27.529261 Long=-97.881585
		//Bellamah Music - Lat=27.524561 Long=-97.879493
		//Biology-Earth Science - Lat=27.524638 Long=-97.881526
		//Business Administration - Lat=27.526902 Long=-97.882808
		//Center for Young Children - Lat=27.521812 Long=-97.882862
		//Clement - Lat=27.529471 Long=-97.879279
		//College Hall - Lat=27.525066 Long=-97.882454
		//College of Pharmacy - Lat=27.526807 Long=-97.881896
	    //Conner Museum -  Lat=27.522168 Long=-97.879493
		//Cousins Hall - Lat=27.522230 Long=-97.880191
		//Dotterweich Engineering - Lat=27.526074 Long=-97.880148
		//Drama/Arts Building - Lat=27.524152 Long=-97.878512
		//Eckhardt Hall - Lat = 27.523410 Long = -97.882969
		//Engineering Complex - Lat=27.526017 Long=-97.878914
		//Health And Recreation - Lat=27.524733 Long=-97.880491
		//Hill Hall            - Lat=27.523781 Long=-97.880287
		//Human Sciences - Lat=27.524752 Long=-97.883635
		//Intramural Fields - Lat=27.529556 Long=-97.883238
		//J.C. Martin Hall - Lat=27.524657 Long=-97.884954
		//JW Howe AG - Lat=27.526103 Long=-97.883688
		//James Jernigan - Lat=27.526008 Long=-97.881875
		//Javelina Dining - Lat=27.525304 Long=-97.884675
		//Javelina House - Lat=27.523068 Long=-97.880931
		//Javelina Stadium - Lat=27.528767 Long=-97.880062
		//John Lynch Hall - Lat=27.522858 Long=-97.883281
		//Jones Auditorium - Lat=27.524209 Long=-97.878420
		//Karr Veterans - Lat=27.525356 Long=-97.879021
		//Kleberg AG - Lat=27.525617 Long=-97.883388
		//Lewis Hall - Lat=27.524162 Long=-97.882926
		//Life Services - Lat=27.527568 Long=-97.883592
		//Manning - Lat=27.523305 Long=-97.879504
		//McCulley Hall - Lat=27.528957 Long=-97.879268
		//McNeil Engineering - Lat=27.525513 Long=-97.879922
		//Memorial SUB - Lat=27.523505 Long=-97.881746
		//Mesquite Grove - Lat=27.527730 Long=-97.885158
		//Mesquite Village - Lat=27.526740 Long=-97.885115
		//Nierman Science - Lat=27.523286 Long=-97.880234
		//Physical Plant - Lat=27.527948 Long=-97.884321
		//Poteet Hall - Lat=27.522230 Long=-97.881778
		//Presidents Home - Lat=27.524794 Long=-97.878233
		//Recreation Center - Lat=27.527815 Long=-97.881816
		//Rhode Hall - Lat=27.526141 Long=-97.882851
		//Sam Fore Hall - Lat=27.524457 Long=-97.880325
		//Seale Hall - Lat=27.522264 Long=-97.878844
		//Speech Building - Lat=27.524171 Long=-97.879107
		//Steike Physical ED - Lat=27.526983 Long=-97.880073
		//Support Services - Lat=27.525851 Long=-97.883989
		//Turner-Bishop Hall - Lat=27.523030 Long=-97.884927 
		//University Village - Lat=27.525832 Long=-97.884783
		
 */
	
	
}
