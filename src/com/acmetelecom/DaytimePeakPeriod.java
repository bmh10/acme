package com.acmetelecom;

import java.math.BigDecimal;

import org.joda.time.DateTime;

import com.acmetelecom.customer.Tariff;

// TODO: add interface for this.
// Holds all information about when peak period starts/ends and methods related to this.
public class DaytimePeakPeriod{

	// TODO: put these in config file??
	public final int PeakStart = 7;
	public final int PeakEnd = 19;
	
	// Defines periods in a day.
	public enum DayPeriod {
		PrePeak,
		Peak,
		PostPeak
	}
    
	public int getPeriodDurationSeconds(DayPeriod period) {
 		switch (period) {
 		case PrePeak:  return PeakStart * 60 * 60;
 		case Peak:     return (PeakEnd - PeakStart) * 60 * 60;
 		case PostPeak: return (24 - PeakEnd) * 60 * 60;
 		default:       return 0;
 		}
 	}
 	
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
 	
 	public BigDecimal getPeriodRate(DayPeriod period, Tariff tariff) {
 		switch (period) {
 		case PrePeak:  return tariff.offPeakRate();
 		case Peak:     return tariff.peakRate();
 		case PostPeak: return tariff.offPeakRate();
 		default:       return new BigDecimal(0);
 		}
 	}
 	
	public DayPeriod getPeriodOfDay(DateTime time) {
		int hour = time.getHourOfDay();
		if (hour < PeakStart) return DayPeriod.PrePeak;
		if (hour < PeakEnd) return DayPeriod.Peak;
		return DayPeriod.PostPeak;	
	}
}
