package com.acmetelecom;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.acmetelecom.customer.Tariff;

// TODO: add interface for this.
/**
 * Holds all information about when peak period starts/ends and methods related to this.
 */
public class DaytimePeakPeriod {

	// TODO: put these in a config file??
	public final int PeakStart = 7;
	public final int PeakEnd = 19;
	
	/**
	 * Defines the period in a day.
	 */
	public enum DayPeriod {
		PrePeak,
		Peak,
		PostPeak
	}
    
	/**
	 * Gets the duration of a specified period in seconds.
	 * @param period The period type to get the duration of.
	 */
	public int getPeriodDurationSeconds(DayPeriod period) {
 		switch (period) {
 		case PrePeak:  return PeakStart * 60 * 60;
 		case Peak:     return (PeakEnd - PeakStart) * 60 * 60;
 		case PostPeak: return (24 - PeakEnd) * 60 * 60;
 		default:       return 0;
 		}
 	}
 	
	/**
	 * Gets the time of day (in seconds) at the end of a specified period.
	 * @param period The period type to get the time of day for.
	 */
 	public int getSecondInDayAtEndOfPeriod(DayPeriod period) {
 		int a = getPeriodDurationSeconds(DayPeriod.PrePeak);
 		int b = getPeriodDurationSeconds(DayPeriod.Peak);
 		int c = getPeriodDurationSeconds(DayPeriod.PrePeak);
 		
 		switch (period) {
 		case PrePeak:  return a;
 		case Peak:     return a + b;
 		case PostPeak: return a + b + c;
 		default:       return 0;
 		}
 	}
 	
 	/**
 	 * Gets the pricing rate for a specific tariff and a specific period of the day.
 	 * @param period The period of the day to get the pricing rate for.
 	 * @param tariff The tariff to get the pricing rate for.
 	 * @return The pricing rate as a BigDecimal.
 	 */
 	public BigDecimal getPeriodRate(DayPeriod period, Tariff tariff) {
 		switch (period) {
 		case PrePeak:  return tariff.offPeakRate();
 		case Peak:     return tariff.peakRate();
 		case PostPeak: return tariff.offPeakRate();
 		default:       return new BigDecimal(0);
 		}
 	}
 	
 	/**
 	 * Gets the DayPeriod associated with a specific time in the day.
 	 * @param time The time to get the DayPeriod for.
 	 * @return The DayPeriod associated with the provided time.
 	 */
	public DayPeriod getPeriodOfDay(DateTime time) {
		int hour = time.getHourOfDay();
		if (hour < PeakStart) return DayPeriod.PrePeak;
		if (hour < PeakEnd) return DayPeriod.Peak;
		return DayPeriod.PostPeak;	
	}
}
