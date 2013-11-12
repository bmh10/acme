package com.acmetelecom;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.acmetelecom.customer.Tariff;

/**
 * Defines the interface for a class describing a peak period.
 */
public interface IPeakPeriod {
		/**
		 * Defines the periods in a day.
		 */
		public enum DayPeriod {
			PrePeak,
			Peak,
			PostPeak
		}
		
		/**
		 * Gets the hour of the peak start time.
		 * @return The hour when the peak period starts.
		 */
		int getPeakStart();
		
		/**
		 * Gets the hour of the peak end time.
		 * @return The hour when the peak period ends.
		 */
		int getPeakEnd();
	    
		/**
		 * Gets the duration of a specified period in seconds.
		 * @param period The period type to get the duration of.
		 * @exception IllegalArgumentException If any of arguments are null.
		 */
		int getPeriodDurationSeconds(DayPeriod period);
	 	
		/**
		 * Gets the time of day (in seconds) at the end of a specified period.
		 * @param period The period type to get the time of day for.
		 * @exception IllegalArgumentException If any of arguments are null.
		 */
	 	int getSecondInDayAtEndOfPeriod(DayPeriod period);
	 	
	 	/**
	 	 * Gets the pricing rate for a specific tariff and a specific period of the day.
	 	 * @param period The period of the day to get the pricing rate for.
	 	 * @param tariff The tariff to get the pricing rate for.
	 	 * @return The pricing rate as a BigDecimal.
	 	 * @exception IllegalArgumentException If any of arguments are null.
	 	 */
	 	BigDecimal getPeriodRate(DayPeriod period, Tariff tariff);
	 	
	 	/**
	 	 * Gets the DayPeriod associated with a specific time in the day.
	 	 * @param time The time to get the DayPeriod for.
	 	 * @return The DayPeriod associated with the provided time.
	 	 * @exception IllegalArgumentException If any of arguments are null.
	 	 */
		DayPeriod getPeriodOfDay(DateTime time);
}
