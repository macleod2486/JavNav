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

package com.senior.javnav;

import android.util.Log;
public class Coordinates 
{
	
	//Latitude of every building in the names array
	private double latit[]=
							{
								27.525846, //Al Gross Hall
								27.525998, //AL Kleberg
								27.529832, //Athletic offices
								27.523838, //Bailey Arts
								27.529261, //Baseball Field
								27.524561, //Bellamuh Music
								27.524638, //Biology Earth-Science
								27.526902, //Business Administration
								27.521812, //Center for Young Children
								27.529471, //Clement
								27.525066, //College Hall
								27.526807, //College of Pharmacy
								27.522168, //Conner Museum
								27.522230, //Cousins Hall
								27.526074, //Dotterweich Engineering
								27.524152, //Drama Arts building
								27.523410, //Eckhardt Hall
								27.526017, //Engineering Complex
								27.524733, //Health & Recreation Center
								27.523781, //Hill hall
								27.524752, //Human Sciences
								27.529556, //Intramural Fields
								27.524657, //JC Martin Hall
								27.526103, //JW Howe AG
								27.526008, //James Jernigan
								27.525304, //Javelina Dining
								27.523068, //Javelina House
								27.528767, //Javelina Stadium
								27.531702, //Javelina Station
								27.522858, //John Lynch Hall
								27.524209, //Jones Auditorium
								27.525356, //Karr Veterans
								27.525617, //Kleberg AG
								27.524162, //Lewis Hall
								27.527568, //Life Services
								27.525832, //Lucio Hall
								27.523305, //Manning Hall
								27.528957, //McCulley Hall
								27.525513, //McNeil Engineering
								27.523505, //Memorial SUB
								27.527730, //Mesquite Grove
								27.526740, //Mesquite Village
								27.523286, //Nierman Science
								27.527948, //Physical Plant
								27.522230, //Poteet Hall
								27.524794, //Presidents Home
								27.527815, //Recreation Center
								27.526141, //Rhode Hall
								27.524457, //Sam Fore Hall
								27.522264, //Seale Hall
								27.524171, //Speech Building
								27.526983, //Steinke Physical ED
								27.525851, //Support Services
								27.523030, //Turner-Bishop Hall
							};
	
	//Longitude of every building in the names array
	private double longit[]=
					  		{
								-97.878292, //AL Gross Hall
								-97.880716, //AL Kleberg Hall
								-97.879300, //Athletic Offices
								-97.879547, //Bailey Arts
								-97.881585, //Baseball Field
								-97.879493, //Belamah Music
								-97.881526, //Biology Earth Science
								-97.882808, //Business Administraton
								-97.882862, //Center for Young Children
								-97.879279, //Clement
								-97.882454, //College Hall
								-97.881896, //College of Pharmacy
								-97.879493, //Conner Museum
								-97.880191, //Cousins Hall
								-97.880148, //Dotterweich Engineering
								-97.878512, //Drama Arts Building
								-97.882969, //Eckhardt Hall
								-97.878914, //Engineering Complex
								-97.880491, //Health and Recreation
								-97.880287, //Hill Hall
								-97.883635, //Human Sciences
								-97.883238, //Intramural Field
								-97.884954, //JC Martin Hall
								-97.883688, //JW Howe Ag
								-97.881875, //James Jernigan
								-97.884675, //Javelina Dining
								-97.880931, //Javelina House
								-97.880062, //Javelina Stadium
								-97.884938, //Javelina Station
								-97.883281, //John Lynch Hall
								-97.878420, //Jones Auditorium
								-97.879021, //Karr Veterans
								-97.883388, //Kleberg Ag
								-97.882926, //Lewis Hall
								-97.883592, //Life Services
								-97.884783, //Lucio Hall
								-97.879504, //Manning Hall
								-97.879268, //McCulley Hall
								-97.879922, //McNeil Engineering
								-97.881746, //Memorial Sub
								-97.885158, //Mesquite Grove
								-97.885115, //Mesquite Village
								-97.880234, //Nierman Science
								-97.884321, //Physical Plant
								-97.881778, //Poteet Hall
								-97.878233, //Presidents Home
								-97.881816, //Recreation Center
								-97.882851, //Rhode Hall
								-97.880325, //Sam Fore Hall
								-97.878844, //Seale Hall
								-97.879107, //Speech Building
								-97.880073, //Steinke Physical ED
								-97.883989, //Support Services
								-97.884927, //Turner-Bishop Hall
					  		};
	
	//Names of all the buildings on campus
	private String names[]=
							{
								"AL Gross Hall",
								"Al Kleberg Hall",
								"Athletic Offices",
								"Bailey Arts",
								"Baseball Field",
								"Bellamah Music",
								"Biology-Earth Science",
								"Business Administration",
								"Center for Young Children",
								"Clement",
								"College Hall",
								"College of Pharmacy",
								"Conner Museum",
								"Cousins Hall",
								"Dotterweich Engineering",
								"Drama/Arts Building",
								"Eckhardt Hall",
								"Engineering Complex",
								"Health And Recreation",
								"Hill Hall",
								"Human Sciences",
								"Intramural Fields",
								"J.C. Martin Hall",
								"JW Howe AG",
								"James Jernigan",
								"Javelina Dining",
								"Javelina House",
								"Javelina Stadium",
								"Javelina Station",
								"John Lynch Hall",
								"Jones Auditorium",
								"Karr Veterans",
								"Kleberg AG",
								"Lewis Hall",
								"Life Services",
								"Lucio Hall",
								"Manning Hall",
								"McCulley Hall",
								"McNeil Engineering",
								"Memorial SUB",
								"Mesquite Grove",
								"Mesquite Village",
								"Nierman Science",
								"Physical Plant",
								"Poteet Hall",
								"Presidents Home",
								"Recreation Center",
								"Rhode Hall",
								"Sam Fore Hall",
								"Seale Hall",
								"Speech Building",
								"Steike Physical ED",
								"Support Services",
								"Turner-Bishop Hall"
							};
			
	//Matches the name of the building to the latitude
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
	//Matches the name of the building to the longitude
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
 		List of all buildings for reference
 		
		//AL Gross Hall - Lat=27.525846 Long=-97.878292
		//Al Kleberg Hall - Lat=27.525998 Long=-97.880716
		//Athletic Offices - Lat=27.529832 Long=-97.879300
		//Bailey Arts - Lat=27.523838 Long=	-97.879547
		//Baseball Field - Lat=27.529261 Long=-97.881585
		//Bellamah Music - Lat=27.524561 Long=-97.879493
		//Biology-Earth Science - Lat=27.524638 Long=-97.881526
		//Business Administration - Lat=27.526902 Long=-97.882808
		//*Center for Young Children - Lat=27.521812 Long=-97.882862
		//Clement - Lat=27.529471 Long=-97.879279
		//College Hall - Lat=27.525066 Long=-97.882454
		//College of Pharmacy - Lat=27.526807 Long=-97.881896
	    //Conner Museum -  Lat=27.522168 Long=-97.879493
		//Cousins Hall - Lat=27.522230 Long=-97.880191
		//Dotterweich Engineering - Lat=27.526074 Long=-97.880148
		//Drama/Arts Building - Lat=27.524152 Long=-97.878512
		//Eckhardt Hall - Lat = 27.523410 Long = -97.882969
		//*Engineering Complex - Lat=27.526017 Long=-97.878914
		//Health And Recreation - Lat=27.524733 Long=-97.880491
		//Hill Hall            - Lat=27.523781 Long=-97.880287
		//Human Sciences - Lat=27.524752 Long=-97.883635
		//Intramural Fields - Lat=27.529556 Long=-97.883238
		//J.C. Martin Hall - Lat=27.524657 Long=-97.884954
		//JW Howe AG - Lat=27.526103 Long=-97.883688
		//James Jernigan - Lat=27.526008 Long=-97.881875
		//Javelina Dining - Lat=27.525304 Long=-97.884675
		//*Javelina House - Lat=27.523068 Long=-97.880931
		//Javelina Stadium - Lat=27.528767 Long=-97.880062
		//Javelina Station - Lat=27.531702 Long=-97.884938
		//John Lynch Hall - Lat=27.522858 Long=-97.883281
		//Jones Auditorium - Lat=27.524209 Long=-97.878420
		//Karr Veterans - Lat=27.525356 Long=-97.879021
		//Kleberg AG - Lat=27.525617 Long=-97.883388
		//Lewis Hall - Lat=27.524162 Long=-97.882926
		//Life Services - Lat=27.527568 Long=-97.883592
		//Lucio Hall - Lat=27.525832 Long=-97.884783
		//*Manning - Lat=27.523305 Long=-97.879504
		//McCulley Hall - Lat=27.528957 Long=-97.879268
		//McNeil Engineering - Lat=27.525513 Long=-97.879922
		//Memorial SUB - Lat=27.523505 Long=-97.881746
		//Mesquite Grove - Lat=27.527730 Long=-97.885158
		//Mesquite Village - Lat=27.526740 Long=-97.885115
		//Nierman Science - Lat=27.523286 Long=-97.880234
		//Physical Plant - Lat=27.527948 Long=-97.884321
		//Poteet Hall - Lat=27.522230 Long=-97.881778
		//*Presidents Home - Lat=27.524794 Long=-97.878233
		//Recreation Center - Lat=27.527815 Long=-97.881816
		//Rhode Hall - Lat=27.526141 Long=-97.882851
		//Sam Fore Hall - Lat=27.524457 Long=-97.880325
		//Seale Hall - Lat=27.522264 Long=-97.878844
		//Speech Building - Lat=27.524171 Long=-97.879107
		//Steike Physical ED - Lat=27.526983 Long=-97.880073
		//Support Services - Lat=27.525851 Long=-97.883989
		//Turner-Bishop Hall - Lat=27.523030 Long=-97.884927 
		
 */
	
	
}
