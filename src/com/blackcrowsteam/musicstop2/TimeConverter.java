package com.blackcrowsteam.musicstop2;
/*
 * Copyright 2012 Laurent Constantin <android@blackcrowsteam.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.res.Resources;
/**
 * Used to output some duration into a friendly string
 * And convert x seconds into X0 days/ X1 hours/ X2 minutes/ X3 seconds
 * @author Constantin Laurent
 *
 */
public class TimeConverter {
	
	// See #loadString for other languages support.
	private static String[] unit = {"day","hour","minute","second"};
	private static String[] units = {"days","hours","minutes","seconds"};
	// Internal check of unit and units length
	private final static int NB_UNIT = 4;
	
	/**
	 * Load strings from strings.xml, so we can use multiple languages
	 * @param r Ressources
	 */
	public static void loadString(Resources r){
		unit = r.getStringArray(R.array.unit);
		units = r.getStringArray(R.array.units);
		
		if(units == null || unit == null || units.length != unit.length || unit.length != NB_UNIT)
			throw new IllegalStateException("unit or units in strings.xml is invalid");
	}
	/**
	 * Return the string from unit[tabindex] or units[tabindex] if the quantitiy is 1 or >1.
	 * Return an empty string if the quantity is 0.
	 * @param quantity 
	 * @param tabIndex
	 * @return
	 */
	private static String getUnit(int quantity, int tabIndex){
		if(tabIndex < 0 || tabIndex > unit.length || unit.length != units.length)
			throw new IndexOutOfBoundsException();
		
		return (quantity == 0 ? "" : quantity+" "+(quantity > 1?units[tabIndex]:unit[tabIndex]));
		
	}
	/**
	 * See the examples above:
	 * tab={1,2,3} then return "1 hour 2 minutes 3 seconds"
	 * tab={1,2,0} then return "1 hour 2 minutes"
	 * tab={0}     then return "0 second"
	 * @param tab
	 * @return
	 */
	private static String format(int ... tab){
		if(unit.length != tab.length)
			throw new IllegalStateException("Something is wrong!");
		
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < tab.length ; i++){
			if(sb.length() != 0)
				sb.append(" ");
			
			sb.append(getUnit(tab[i],i));
			
		}
		if(sb.length() == 0)
			sb.append("0 ").append(unit[unit.length-1]);
		
		return sb.toString();
		
		
		
	}
	/**
	 * Return the number of days
	 * @param numberOfSeconds
	 * @return x days
	 */
	public static int getNumberOfDays(int numberOfSeconds){
		return numberOfSeconds / 86400;

	}
	/**
	 * Return the number of hours
	 * @param numberOfSeconds
	 * @return 0-23 hours
	 */
	public static int getNumberOfHours(int numberOfSeconds){
		return (numberOfSeconds % 86400)/ 3600;
	}
	/**
	 * Return the number of minutes
	 * @param numberOfSeconds
	 * @return 0-59 minutes
	 */
	public static int getNumberOfMinutes(int numberOfSeconds){
		return (numberOfSeconds % 3600) / 60 ;
		
	}
	/**
	 * Return the number of seconds
	 * @param numberOfSeconds
	 * @return 0-59 seconds
	 */
	public static int getNumberOfSeconds(int numberOfSeconds){
		numberOfSeconds = (numberOfSeconds % 86400) % 3600;
		return numberOfSeconds % 60;	
	}
	/**
	 * Convert x seconds into a string like "1 day 2 hours 23 minutes 1 second"
	 * @param numberOfSeconds Duration to convert
	 * @return A friendly string
	 */
	public static String time(int numberOfSeconds){
		int numberOfDays = numberOfSeconds / 86400;
		numberOfSeconds = numberOfSeconds % 86400;
		int numberOfHours = numberOfSeconds / 3600;
		numberOfSeconds = numberOfSeconds % 3600;
		int numberOfMinutes = numberOfSeconds / 60;
		numberOfSeconds = numberOfSeconds % 60;
		
		return format(numberOfDays, numberOfHours, numberOfMinutes, numberOfSeconds);
		
	}
}
